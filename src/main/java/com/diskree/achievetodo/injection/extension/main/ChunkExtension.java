package com.diskree.achievetodo.injection.extension.main;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
import java.util.Map;

public interface ChunkExtension {

    void achievetodo$setFeatureBlockBoxes(ServerWorld world, Map<Feature<?>, List<BlockBox>> featureBlockBoxes);

    Map<Feature<?>, List<BlockBox>> achievetodo$getFeatureBlockBoxes();

    void achievetodo$addFeatureBlockBox(ServerWorld world, Feature<?> feature, BlockBox box);
}
