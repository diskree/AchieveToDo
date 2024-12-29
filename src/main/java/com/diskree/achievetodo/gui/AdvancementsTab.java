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
        return Identifier.of(BuildConfig.MOD_ID + "_locked_tab", getLowerCaseName() + "/root");
    }

    public @NotNull Text getLockedTabTooltipText() {
        return Text.translatable("achievetodo.locked_tab_tooltip." + getLowerCaseName())
            .formatted(Formatting.ITALIC)
            .formatted(Formatting.GRAY);
    }

    public @NotNull String getLowerCaseName() {
        return name().toLowerCase();
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
