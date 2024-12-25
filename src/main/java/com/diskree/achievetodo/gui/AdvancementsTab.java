package com.diskree.achievetodo.gui;

import com.diskree.achievetodo.BuildConfig;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AdvancementsTab {

    BIOMES(AdvancementTabType.LEFT),
    ADVENTURE(AdvancementTabType.LEFT),
    WEAPONRY(AdvancementTabType.LEFT),
    HUSBANDRY(AdvancementTabType.LEFT),
    MONSTERS(AdvancementTabType.LEFT),

    ABILITIES(AdvancementTabType.ABOVE),
    MINING(AdvancementTabType.ABOVE),
    BUILDING(AdvancementTabType.ABOVE),
    FARMING(AdvancementTabType.ABOVE),
    NETHER(AdvancementTabType.ABOVE),
    END(AdvancementTabType.ABOVE),

    REDSTONE(AdvancementTabType.BELOW),
    POTION(AdvancementTabType.BELOW),
    ENCHANTING(AdvancementTabType.BELOW),
    CHALLENGES(AdvancementTabType.BELOW),
    STATISTICS(AdvancementTabType.BELOW),
    BACAP(AdvancementTabType.BELOW);

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
        return Identifier.of(BuildConfig.MOD_ID + "_locked_tab", name().toLowerCase() + "/root");
    }

    public @NotNull Text getLockedTabTooltipText() {
        return Text.translatable("achievetodo.locked_tab_tooltip." + name().toLowerCase())
            .formatted(Formatting.ITALIC)
            .formatted(Formatting.GRAY);
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
