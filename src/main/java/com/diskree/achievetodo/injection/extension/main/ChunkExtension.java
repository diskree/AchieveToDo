package com.diskree.achievetodo.injection.extension.main;

import com.diskree.achievetodo.ability.DimensionalBlockBox;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Map;

public interface ChunkExtension {

    void achievetodo$setFeatureLandmarks(ServerWorld world, Map<LandmarkType, List<DimensionalBlockBox>> landmarks);

    Map<LandmarkType, List<DimensionalBlockBox>> achievetodo$getFeatureLandmarks();

    void achievetodo$addFeatureLandmark(ServerWorld world, LandmarkType landmarkType, DimensionalBlockBox dimensionalBlockBox);
}
