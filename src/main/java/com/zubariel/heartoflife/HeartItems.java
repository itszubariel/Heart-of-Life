package com.zubariel.heartoflife;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class HeartItems {

    public static final Identifier HEART_ID = Identifier.of("heartoflife", "heart");
    public static final RegistryKey<Item> HEART_KEY = RegistryKey.of(RegistryKeys.ITEM, HEART_ID);
    public static final Item HEART = new HeartItem(
            new Item.Settings()
                    .registryKey(HEART_KEY)
                    .maxCount(1)
                    .fireproof()
    );

    public static final Identifier HEART_FRAGMENT_ID = Identifier.of("heartoflife", "heart_fragment");
    public static final RegistryKey<Item> HEART_FRAGMENT_KEY = RegistryKey.of(RegistryKeys.ITEM, HEART_FRAGMENT_ID);
    public static final Item HEART_FRAGMENT = new HeartFragmentItem(
            new Item.Settings()
                    .registryKey(HEART_FRAGMENT_KEY)
                    .maxCount(16)
                    .fireproof()
    );


    public static void registerItems() {
        Registry.register(Registries.ITEM, HEART_KEY, HEART);
        Registry.register(Registries.ITEM, HEART_FRAGMENT_KEY, HEART_FRAGMENT);
    }
}