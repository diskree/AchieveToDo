package com.diskree.achievetodo.client.gui;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.client.AchieveToDoClient;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum AdvancementsTab {

    ABILITIES(AdvancementTabType.LEFT),
    BACAP(AdvancementTabType.LEFT),
    STATISTICS(AdvancementTabType.LEFT),

    BUILDING(AdvancementTabType.ABOVE),
    FARMING(AdvancementTabType.ABOVE),
    HUSBANDRY(AdvancementTabType.ABOVE),
    BIOMES(AdvancementTabType.ABOVE),
    ADVENTURE(AdvancementTabType.ABOVE),
    MONSTERS(AdvancementTabType.ABOVE),
    WEAPONRY(AdvancementTabType.ABOVE),

    MINING(AdvancementTabType.BELOW),
    REDSTONE(AdvancementTabType.BELOW),
    ENCHANTING(AdvancementTabType.BELOW),
    NETHER(AdvancementTabType.BELOW),
    POTION(AdvancementTabType.BELOW),
    END(AdvancementTabType.BELOW),
    CHALLENGES(AdvancementTabType.BELOW);

    private final AdvancementTabType position;

    AdvancementsTab(AdvancementTabType position) {
        this.position = position;
    }

    public AdvancementTabType getPosition() {
        return position;
    }

    public int getOrder() {
        int order = 0;
        for (AdvancementsTab tab : AdvancementsTab.values()) {
            if (tab.position == position) {
                if (tab == this) {
                    break;
                }
                order++;
            }
        }
        return order;
    }

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
}
