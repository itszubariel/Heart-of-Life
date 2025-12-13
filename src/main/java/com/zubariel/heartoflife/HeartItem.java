package com.zubariel.heartoflife;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class HeartItem extends Item {
    public HeartItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        MutableText text = (MutableText) super.getName(stack);
        return text.styled(style -> style.withColor(Formatting.RED));
    }
}
