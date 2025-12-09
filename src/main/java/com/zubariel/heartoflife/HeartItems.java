package com.zubariel.heartoflife;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class HeartItems {

    public static final Identifier HEART_ID = Identifier.of("heartoflife", "heart");
    public static final RegistryKey<Item> HEART_KEY = RegistryKey.of(RegistryKeys.ITEM, HEART_ID);

    public static final Item HEART = new HeartItem(
            new Item.Settings()
                    .registryKey(HEART_KEY)
                    .maxCount(16)
    );

    public static void registerItems() {
        Registry.register(Registries.ITEM, HEART_KEY, HEART);
    }
}
