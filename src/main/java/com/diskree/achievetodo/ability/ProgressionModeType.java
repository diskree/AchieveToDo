package com.diskree.achievetodo.ability;

import com.diskree.achievetodo.client.AchieveToDoClient;
import net.minecraft.text.Text;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ProgressionModeType {

    EASY(1),
    NORMAL(1),
    HARD(1),
    CHAOS(1);

    private final int version;

    ProgressionModeType(int version) {
        this.version = version;
    }

    public static ProgressionModeType getDefaultMode() {
        return NORMAL;
    }

    public static @Nullable ProgressionModeType findByName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        for (ProgressionModeType progressionModeType : values()) {
            if (progressionModeType.name().equalsIgnoreCase(name)) {
                return progressionModeType;
            }
        }
        return null;
    }

    public int getVersion() {
        return version;
    }

    public @NotNull Text getDisplayedText() {
        if (this == CHAOS) {
            return AchieveToDoClient.translate("world_creation_tab.progression.chaos");
        }
        return Text.translatable("options.difficulty." + getName());
    }

    public @NotNull Text getTooltipText() {
        return AchieveToDoClient.translate("world_creation_tab.progression." + getName() + ".tooltip");
    }

    public @NotNull String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
