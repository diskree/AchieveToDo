package com.diskree.achievetodo.client.gui;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.client.AchieveToDoClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum AdvancementsTab {

    ABILITIES,
    BACAP,
    STATISTICS,

    BUILDING,
    FARMING,
    HUSBANDRY,
    BIOMES,
    ADVENTURE,
    MONSTERS,
    WEAPONRY,

    MINING,
    REDSTONE,
    ENCHANTING,
    NETHER,
    POTION,
    END,
    CHALLENGES;

    public @NotNull Identifier getLockedTabId() {
        return Identifier.of(BuildConfig.MOD_ID + "_locked_tab", getName() + "/root");
    }

    public @NotNull Text getLockedTabTooltipText() {
        return AchieveToDoClient.translateModKey("locked_tab_tooltip." + getName())
            .formatted(Formatting.ITALIC)
            .formatted(Formatting.GRAY);
    }

    public @NotNull String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static @Nullable AdvancementsTab findByAdvancement(@NotNull PlacedAdvancement advancement) {
        return findByAdvancement(advancement.getAdvancementEntry().id());
    }

    public static @Nullable AdvancementsTab findByAdvancement(@NotNull Identifier advancementId) {
        String[] pathSlices = advancementId.getPath().split("/");
        if (pathSlices.length == 2) {
            for (AdvancementsTab tabType : values()) {
                if (tabType.name().equalsIgnoreCase(pathSlices[0])) {
                    return tabType;
                }
            }
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public AdvancementTabType getPosition() {
        if (this == ABILITIES || this == BACAP || this == STATISTICS) {
            return AdvancementTabType.LEFT;
        }
        if (this == BUILDING ||
            this == FARMING ||
            this == HUSBANDRY ||
            this == BIOMES ||
            this == ADVENTURE ||
            this == MONSTERS ||
            this == WEAPONRY
        ) {
            return AdvancementTabType.ABOVE;
        }
        return AdvancementTabType.BELOW;
    }

    @Environment(EnvType.CLIENT)
    public int getOrder() {
        int order = 0;
        AdvancementTabType position = getPosition();
        for (AdvancementsTab tab : AdvancementsTab.values()) {
            if (tab.getPosition() == position) {
                if (tab == this) {
                    break;
                }
                order++;
            }
        }
        return order;
    }
}
