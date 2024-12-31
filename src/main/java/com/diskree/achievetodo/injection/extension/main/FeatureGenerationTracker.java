package com.diskree.achievetodo.injection.extension.main;

import net.minecraft.util.math.BlockBox;

public interface FeatureGenerationTracker {
    void achievetodo$setTrackFeatureGeneration(boolean shouldTrackFeatureGeneration);

    BlockBox achievetodo$getFeatureBlockBox();
}
