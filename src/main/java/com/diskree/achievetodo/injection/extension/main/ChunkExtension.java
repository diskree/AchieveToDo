package com.diskree.achievetodo.injection.extension.main;

import com.diskree.achievetodo.ability.DimensionalBlockBox;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Set;

public interface ChunkExtension {

    void achievetodo$setFeatureLandmarks(ServerWorld world, Map<LandmarkType, Set<DimensionalBlockBox>> landmarks);

    Map<LandmarkType, Set<DimensionalBlockBox>> achievetodo$getFeatureLandmarks();

    void achievetodo$addFeatureLandmark(
        ServerWorld world,
        LandmarkType landmarkType,
        DimensionalBlockBox dimensionalBlockBox
    );
}
