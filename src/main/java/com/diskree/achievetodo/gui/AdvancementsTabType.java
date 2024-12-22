package com.diskree.achievetodo.gui;

import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.util.Identifier;

public enum AdvancementsTabType {

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
    STATISTICS(AdvancementTabType.RIGHT),
    BACAP(AdvancementTabType.RIGHT),

    REDSTONE(AdvancementTabType.BELOW),
    POTION(AdvancementTabType.BELOW),
    ENCHANTING(AdvancementTabType.BELOW),
    CHALLENGES(AdvancementTabType.BELOW);

    private final AdvancementTabType position;

    AdvancementsTabType(AdvancementTabType position) {
        this.position = position;
    }

    public AdvancementTabType getPosition() {
        return position;
    }

    public static AdvancementsTabType findByAdvancement(Identifier advancementId) {
        String[] pathSlices = advancementId.getPath().split("/");
        if (pathSlices.length == 2) {
            for (AdvancementsTabType tabType : values()) {
                if (tabType.name().equalsIgnoreCase(pathSlices[0])) {
                    return tabType;
                }
            }
        }
        return null;
    }

    public int getOrder() {
        int order = 0;
        for (AdvancementsTabType tab : AdvancementsTabType.values()) {
            if (tab.position == position) {
                if (tab == this) {
                    break;
                }
                order++;
            }
        }
        return order;
    }
}
