package com.diskree.achievetodo.client.gui;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum AbilityUnlockedToastType {

    ACTION,
    ITEM,
    FOOD,
    TOOL,
    WEAPON,
    EQUIPMENT,
    BLOCK,
    TRADING,
    PORTAL;

    public @NotNull Text getToastTitle() {
        return Text.translatable("achievetodo.ability_unlocked_toast." + getLowerCaseName())
            .append("!");
    }

    private @NotNull String getLowerCaseName() {
        return name().toLowerCase();
    }
}
