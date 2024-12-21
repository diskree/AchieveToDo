package com.diskree.achievetodo;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum AbilityCategory {

    ACTION,
    FOOD,
    ITEM,
    BLOCK,
    TOOL,
    EQUIPMENT,
    PORTAL,
    VILLAGER;

    public @NotNull Text getUnblockPopupTitle() {
        return Text.translatable("achievetodo.unlocked." + getName()).append("!");
    }

    public @NotNull String getName() {
        return name().toLowerCase();
    }
}
