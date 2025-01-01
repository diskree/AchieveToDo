package com.diskree.achievetodo.ability;

import com.diskree.achievetodo.client.AchieveToDoClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum DifficultyType {

    EASY(1),
    NORMAL(1),
    HARD(1),
    CHAOS(1);

    private final int version;

    DifficultyType(int version) {
        this.version = version;
    }

    public static DifficultyType findByName(String name) {
        if (name == null) {
            return null;
        }
        for (DifficultyType difficultyType : values()) {
            if (difficultyType.name().equalsIgnoreCase(name)) {
                return difficultyType;
            }
        }
        return null;
    }

    public int getVersion() {
        return version;
    }

    public @NotNull Text getDisplayedText() {
        if (this == CHAOS) {
            return AchieveToDoClient.translateModKey("world_creation_tab.difficulty.chaos");
        }
        return Text.translatable("options.difficulty." + getName());
    }

    public @NotNull Text getTooltipText() {
        return AchieveToDoClient.translateModKey("world_creation_tab.difficulty." + getName() + ".tooltip");
    }

    public @NotNull String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
