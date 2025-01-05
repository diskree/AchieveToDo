package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.DimensionalBlockBox;
import com.diskree.achievetodo.ability.LandmarkType;
import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements ChunkExtension {

    @Unique
    private Map<LandmarkType, List<DimensionalBlockBox>> featureLandmarks;

    @Override
    public void achievetodo$setFeatureLandmarks(
        @NotNull ServerWorld world,
        Map<LandmarkType, List<DimensionalBlockBox>> featureLandmarks
    ) {
        this.featureLandmarks = featureLandmarks;
        if (featureLandmarks != null) {
            AchieveToDoMod.getServer().onLandmarksLoadedStatusChanged(world, pos, featureLandmarks, true);
        }
        markNeedsSaving();
    }

    @Override
    public Map<LandmarkType, List<DimensionalBlockBox>> achievetodo$getFeatureLandmarks() {
        return featureLandmarks;
    }

    @Override
    public void achievetodo$addFeatureLandmark(
        @NotNull ServerWorld world,
        LandmarkType featureLandmarkType,
        DimensionalBlockBox dimensionalBlockBox
    ) {
        if (featureLandmarks == null) {
            featureLandmarks = new HashMap<>();
        }
        featureLandmarks
            .computeIfAbsent(featureLandmarkType, k -> new ArrayList<>())
            .add(dimensionalBlockBox);
        AchieveToDoMod.getServer().onLandmarksLoadedStatusChanged(
            world,
            pos,
            Map.of(featureLandmarkType, List.of(dimensionalBlockBox)),
            true
        );
        markNeedsSaving();
    }

    @Shadow
    @Final
    protected ChunkPos pos;

    @Shadow
    public abstract void markNeedsSaving();
}
