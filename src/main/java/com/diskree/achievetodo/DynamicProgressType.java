package com.diskree.achievetodo;

import net.minecraft.advancement.AdvancementRequirements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum DynamicProgressType {

    ;

    private final String criterionName;
    private final String objectiveName;
    private final int finalValue;
    private final boolean isPercentage;

    DynamicProgressType(String criterionName, String objectiveName, int finalValue, boolean isPercentage) {
        this.criterionName = criterionName;
        this.objectiveName = objectiveName;
        this.finalValue = finalValue;
        this.isPercentage = isPercentage;
    }

    public int getFinalValue() {
        return finalValue;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    @Nullable
    public static DynamicProgressType findByAdvancementRequirements(
        @NotNull AdvancementRequirements advancementRequirements
    ) {
        if (advancementRequirements.requirements().size() != 1) {
            return null;
        }
        List<String> list = advancementRequirements.requirements().getFirst();
        if (list.size() != 1) {
            return null;
        }
        for (DynamicProgressType progress : DynamicProgressType.values()) {
            if (progress.criterionName.equals(list.getFirst())) {
                return progress;
            }
        }
        return null;
    }

    public static @Nullable DynamicProgressType findByObjectiveName(String objectiveName) {
        for (DynamicProgressType progress : values()) {
            if (objectiveName.equals(progress.objectiveName)) {
                return progress;
            }
        }
        return null;
    }
}
