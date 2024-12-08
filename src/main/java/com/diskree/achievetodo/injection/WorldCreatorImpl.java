package com.diskree.achievetodo.injection;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface WorldCreatorImpl {

    boolean achievetodo$isItemRewardsEnabled();

    boolean achievetodo$isExperienceRewardsEnabled();

    boolean achievetodo$isTrophyRewardsEnabled();

    boolean achievetodo$isTerralithEnabled();

    boolean achievetodo$isAmplifiedNetherEnabled();

    boolean achievetodo$isNullscapeEnabled();

    boolean achievetodo$isCooperativeModeEnabled();

    void achievetodo$setItemRewardsEnabled(boolean itemRewardsEnabled);

    void achievetodo$setExperienceRewardsEnabled(boolean experienceRewardsEnabled);

    void achievetodo$setTrophyRewardsEnabled(boolean trophyRewardsEnabled);

    void achievetodo$setTerralithEnabled(boolean terralithEnabled);

    void achievetodo$setAmplifiedNetherEnabled(boolean amplifiedNetherEnabled);

    void achievetodo$setNullscapeEnabled(boolean nullscapeEnabled);

    void achievetodo$setCooperativeModeEnabled(boolean cooperativeModeEnabled);
}
