package com.diskree.achievetodo.ability;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum DifficultyType {

    EASY(1),
    NORMAL(1),
    HARD(1);

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

    public @NotNull Text getName() {
        return Text.translatable("options.difficulty." + getLowerCaseName());
    }

    public @NotNull String getLowerCaseName() {
        return name().toLowerCase();
    }
}
