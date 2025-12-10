package com.zubariel.heartoflife;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HeartOfLifeMod implements ModInitializer {

    private static final double HEALTH_LOSS = 2.0;
    private static final double MIN_MAX_HEALTH = 2.0;
    private static final double MAX_MAX_HEALTH = 40.0;
    private static final double HEALTH_GAIN = 2.0;
    private static final Set<UUID> recentlyDied = new HashSet<>();

    public static final RegistryKey<ItemGroup> HEART_GROUP = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of("heartoflife", "heart_group")
    );

    private boolean hasAllItems(ServerPlayerEntity player, Item... items) {
        for (Item item : items) {
            boolean found = false;

            for (int i = 0; i < player.getInventory().size(); i++) {
                if (player.getInventory().getStack(i).getItem() == item) {
                    found = true;
                    break;
                }
            }
            if (!found && player.getOffHandStack().getItem() == item) {
                found = true;
            }

            if (!found) return false;
        }
        return true;
    }

    @Override
    public void onInitialize() {
        HeartItems.registerItems();

        Registry.register(
                Registries.ITEM_GROUP,
                HEART_GROUP,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(HeartItems.HEART))
                        .displayName(Text.literal("Heart of Life"))
                        .entries((displayContext, entries) -> {
                            entries.add(HeartItems.HEART);
                            entries.add(HeartItems.HEART_FRAGMENT);
                        })
                        .build()
        );

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
                if (player.getStackInHand(hand).getItem() == HeartItems.HEART) {
                    EntityAttributeInstance maxHealthAttribute = serverPlayer.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                    if (maxHealthAttribute != null) {
                        double currentHealth = maxHealthAttribute.getBaseValue();
                        if (currentHealth < MAX_MAX_HEALTH) {
                            double newHealth = Math.min(currentHealth + HEALTH_GAIN, MAX_MAX_HEALTH);
                            maxHealthAttribute.setBaseValue(newHealth);
                            serverPlayer.setHealth((float) newHealth);
                            player.getStackInHand(hand).decrement(1);
                            serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
                            serverPlayer.sendMessage(
                                    Text.literal("❤ You gained a permanent heart!").formatted(Formatting.RED),
                                    false
                            );

                            if (world instanceof ServerWorld serverWorld) {
                                Vec3d pos = serverPlayer.getPos();
                                serverWorld.spawnParticles(ParticleTypes.HEART, pos.x, pos.y + 1, pos.z, 5, 0.5, 0.5, 0.5, 0.1);
                            }

                            grantAdvancement(serverPlayer, "whole_again", "use_heart");
                            if (newHealth == MAX_MAX_HEALTH) {
                                grantAdvancement(serverPlayer, "immortal", "immortal");
                            }
                            grantAdvancement(serverPlayer, "hol", "any_advancement");

                            return ActionResult.SUCCESS;
                        } else {
                            serverPlayer.sendMessage(
                                    Text.literal("⚠ Maximum hearts reached!").formatted(Formatting.YELLOW),
                                    false
                            );
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                if (hasAllItems(player, Items.TOTEM_OF_UNDYING, Items.NETHERITE_SCRAP, Items.DIAMOND)) {
                    RegistryKey<Recipe<?>> fragmentRecipeKey = RegistryKey.of(
                            RegistryKeys.RECIPE, Identifier.of("heartoflife", "heart_fragment"));
                    player.unlockRecipes(List.of(fragmentRecipeKey));
                }

                if (hasAllItems(player, HeartItems.HEART_FRAGMENT, Items.HEART_OF_THE_SEA)) {
                    RegistryKey<Recipe<?>> heartRecipeKey = RegistryKey.of(
                            RegistryKeys.RECIPE, Identifier.of("heartoflife", "heart"));
                    player.unlockRecipes(List.of(heartRecipeKey));
                }

                if (player.getHealth() <= 0 && !recentlyDied.contains(player.getUuid())) {
                    recentlyDied.add(player.getUuid());
                    handlePlayerDeath(player);
                } else if (player.getHealth() > 0) {
                    recentlyDied.remove(player.getUuid());
                }

                if (player.getInventory().count(HeartItems.HEART_FRAGMENT) > 0) {
                    grantAdvancement(player, "shattered", "get_fragment");
                    grantAdvancement(player, "hol", "any_advancement");
                }

                if (player.getInventory().count(HeartItems.HEART) > 0) {
                    grantAdvancement(player, "first_heart", "craft_heart");
                    grantAdvancement(player, "hol", "any_advancement");
                }

                int heartCount = 0;
                for (int i = 0; i < 36; i++) {
                    if (player.getInventory().getStack(i).getItem() == HeartItems.HEART) {
                        heartCount++;
                    }
                }
                if (player.getOffHandStack().getItem() == HeartItems.HEART) {
                    heartCount++;
                }
                if (heartCount == 37) {
                    grantAdvancement(player, "moh", "master_of_hearts");
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

                if (newHealth == MIN_MAX_HEALTH) {
                    grantAdvancement(player, "last_breath", "one_heart_left");
                    grantAdvancement(player, "hol", "any_advancement");
                }
            }
        }
    }

    private void grantAdvancement(ServerPlayerEntity player, String advancementId, String criterion) {
        AdvancementEntry entry = player.getServer()
                .getAdvancementLoader()
                .get(Identifier.of("heartoflife", advancementId));
        if (entry != null) {
            AdvancementProgress progress = player.getAdvancementTracker().getProgress(entry);
            if (progress != null) {
                for (String c : progress.getUnobtainedCriteria()) {
                    if (c.equals(criterion)) {
                        player.getAdvancementTracker().grantCriterion(entry, criterion);
                        break;
                    }
                }
            }
        }
    }
}
