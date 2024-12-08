package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.injection.WorldCreatorImpl;
import net.minecraft.client.gui.screen.world.WorldCreator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldCreator.class)
public class WorldCreatorMixin implements WorldCreatorImpl {

    @Unique
    private boolean isItemRewardsEnabled;

    @Unique
    private boolean isExperienceRewardsEnabled;

    @Unique
    private boolean isTrophyRewardsEnabled = true;

    @Unique
    private boolean isTerralithEnabled;

    @Unique
    private boolean isAmplifiedNetherEnabled;

    @Unique
    private boolean isNullscapeEnabled;

    @Unique
    private boolean isCooperativeModeEnabled = true;

    @Override
    public boolean achievetodo$isItemRewardsEnabled() {
        return isItemRewardsEnabled;
    }

    @Override
    public boolean achievetodo$isExperienceRewardsEnabled() {
        return isExperienceRewardsEnabled;
    }

    @Override
    public boolean achievetodo$isTrophyRewardsEnabled() {
        return isTrophyRewardsEnabled;
    }

    @Override
    public boolean achievetodo$isTerralithEnabled() {
        return isTerralithEnabled;
    }

    @Override
    public boolean achievetodo$isAmplifiedNetherEnabled() {
        return isAmplifiedNetherEnabled;
    }

    @Override
    public boolean achievetodo$isNullscapeEnabled() {
        return isNullscapeEnabled;
    }

    @Override
    public boolean achievetodo$isCooperativeModeEnabled() {
        return isCooperativeModeEnabled;
    }

    @Override
    public void achievetodo$setItemRewardsEnabled(boolean itemRewardsEnabled) {
        isItemRewardsEnabled = itemRewardsEnabled;
    }

    @Override
    public void achievetodo$setExperienceRewardsEnabled(boolean experienceRewardsEnabled) {
        isExperienceRewardsEnabled = experienceRewardsEnabled;
    }

    @Override
    public void achievetodo$setTrophyRewardsEnabled(boolean trophyRewardsEnabled) {
        isTrophyRewardsEnabled = trophyRewardsEnabled;
    }

    @Override
    public void achievetodo$setTerralithEnabled(boolean terralithEnabled) {
        isTerralithEnabled = terralithEnabled;
    }

    @Override
    public void achievetodo$setAmplifiedNetherEnabled(boolean amplifiedNetherEnabled) {
        isAmplifiedNetherEnabled = amplifiedNetherEnabled;
    }

    @Override
    public void achievetodo$setNullscapeEnabled(boolean nullscapeEnabled) {
        isNullscapeEnabled = nullscapeEnabled;
    }

    @Override
    public void achievetodo$setCooperativeModeEnabled(boolean cooperativeModeEnabled) {
        isCooperativeModeEnabled = cooperativeModeEnabled;
    }
}
