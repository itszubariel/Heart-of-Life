package com.zubariel.heartoflife.client;

import com.zubariel.heartoflife.HeartItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HeartOfLifeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {

            if (stack.getItem() == HeartItems.HEART) {
                if (Screen.hasShiftDown()) {
                    lines.add(Text.literal("Grants a permanent heart.").formatted(Formatting.GOLD));
                    lines.add(Text.literal("Use wisely—death makes it precious.").formatted(Formatting.DARK_RED));
                } else {
                    lines.add(Text.literal("Hold [Shift] to reveal its power").formatted(Formatting.GRAY));
                }
            }
            else if (stack.getItem() == HeartItems.HEART_FRAGMENT) {
                if (Screen.hasShiftDown()) {
                    lines.add(Text.literal("Collect 4 to forge a Heart of Life.").formatted(Formatting.GOLD));
                    lines.add(Text.literal("Fragments pulse with life, fleeting and fragile.").formatted(Formatting.DARK_RED));
                } else {
                    lines.add(Text.literal("Hold [Shift] to see its secret").formatted(Formatting.GRAY));
                }
            }
        });
    }
}
