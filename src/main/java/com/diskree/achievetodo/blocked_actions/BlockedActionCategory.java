package com.diskree.achievetodo.blocked_actions;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum BlockedActionCategory {

    ACTION,
    FOOD,
    ITEM,
    BLOCK,
    TOOL,
    EQUIPMENT,
    DIMENSION,
    VILLAGER;

    public @NotNull Text getUnblockPopupTitle() {
        return Text.translatable("achievetodo.unblocked." + getName()).append("!");
    }

    public @NotNull String getName() {
        return name().toLowerCase();
    }
}
