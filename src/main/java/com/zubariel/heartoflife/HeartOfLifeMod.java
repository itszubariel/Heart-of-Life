package com.zubariel.heartoflife;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemGroups;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HeartOfLifeMod implements ModInitializer {

    private static final double HEALTH_LOSS = 2.0;
    private static final double MIN_MAX_HEALTH = 2.0;
    private static final double MAX_MAX_HEALTH = 40.0; 
    private static final double HEALTH_GAIN = 2.0;
    private static final Set<UUID> recentlyDied = new HashSet<>();

    @Override
    public void onInitialize() {
        HeartItems.registerItems();

        
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(HeartItems.HEART);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.add(HeartItems.HEART_FRAGMENT);
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player.getStackInHand(hand).getItem() == HeartItems.HEART) {
                if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
                    EntityAttributeInstance maxHealthAttribute = serverPlayer.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                    if (maxHealthAttribute != null) {
                        double currentHealth = maxHealthAttribute.getBaseValue();
                        if (currentHealth < MAX_MAX_HEALTH) {
                            double newHealth = Math.min(currentHealth + HEALTH_GAIN, MAX_MAX_HEALTH);
                            maxHealthAttribute.setBaseValue(newHealth);
                            serverPlayer.setHealth((float) newHealth);
                            player.getStackInHand(hand).decrement(1);

                            
                            serverPlayer.addStatusEffect(
                                    new StatusEffectInstance(
                                            StatusEffects.REGENERATION,
                                            200, 
                                            0    
                                    )
                            );

                            
                            serverPlayer.sendMessage(Text.literal("❤ You gained a permanent heart!"), false);

                            
                            if (world instanceof ServerWorld serverWorld) {
                                Vec3d pos = serverPlayer.getPos();
                                serverWorld.spawnParticles(
                                        ParticleTypes.HEART,
                                        pos.x,
                                        pos.y + 1,
                                        pos.z,
                                        5,    
                                        0.5,  
                                        0.5,  
                                        0.5,  
                                        0.1   
                                );
                            }

                            return ActionResult.SUCCESS;
                        } else {
                            serverPlayer.sendMessage(Text.literal("⚠ You already have maximum hearts!"), false);
                        }
                    }
                }
                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });

        
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getHealth() <= 0 && !recentlyDied.contains(player.getUuid())) {
                    recentlyDied.add(player.getUuid());
                    handlePlayerDeath(player);
                } else if (player.getHealth() > 0) {
                    recentlyDied.remove(player.getUuid());
                }
            }
        });
    }

    private void handlePlayerDeath(ServerPlayerEntity player) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (maxHealthAttribute != null) {
            double currentHealth = maxHealthAttribute.getBaseValue();
            if (currentHealth > MIN_MAX_HEALTH) {
                double newHealth = Math.max(currentHealth - HEALTH_LOSS, MIN_MAX_HEALTH);
                maxHealthAttribute.setBaseValue(newHealth);
            }
        }
    }
}
