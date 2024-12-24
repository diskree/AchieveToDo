package com.diskree.achievetodo.gui;

import com.diskree.achievetodo.BuildConfig;
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

    MINING(AdvancementTabType.ABOVE),
    BUILDING(AdvancementTabType.ABOVE),
    FARMING(AdvancementTabType.ABOVE),
    NETHER(AdvancementTabType.ABOVE),
    END(AdvancementTabType.ABOVE),

    ABILITIES(AdvancementTabType.RIGHT),
    BACAP(AdvancementTabType.RIGHT),

    STATISTICS(AdvancementTabType.BELOW),
    REDSTONE(AdvancementTabType.BELOW),
    POTION(AdvancementTabType.BELOW),
    ENCHANTING(AdvancementTabType.BELOW),
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

    public @NotNull Identifier getPendingTabId() {
        return Identifier.of(BuildConfig.MOD_ID + "_pending", name().toLowerCase() + "/root");
    }

    public @NotNull Text getPendingHelp() {
        return Text.translatable("achievetodo.pending_tab." + name().toLowerCase())
            .formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
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
