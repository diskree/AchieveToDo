package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.LandmarkType;
import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
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
    private Map<Feature<?>, List<BlockBox>> featureBlockBoxes;

    @Override
    public void achievetodo$setFeatureBlockBoxes(
        ServerWorld world,
        @NotNull Map<Feature<?>, List<BlockBox>> featureBlockBoxes
    ) {
        this.featureBlockBoxes = featureBlockBoxes;

        for (Map.Entry<Feature<?>, List<BlockBox>> featureBlockBoxEntry : featureBlockBoxes.entrySet()) {
            LandmarkType landmark = LandmarkType.findByFeature(featureBlockBoxEntry.getKey());
            if (landmark != null) {
                for (BlockBox blockBox : featureBlockBoxEntry.getValue()) {
                    AchieveToDoMod.getServer().onLandmarkLoadedStatusChanged(world, pos, landmark, blockBox, true);
                }
            }
        }
        markNeedsSaving();
    }

    @Override
    public Map<Feature<?>, List<BlockBox>> achievetodo$getFeatureBlockBoxes() {
        return featureBlockBoxes;
    }

    @Override
    public void achievetodo$addFeatureBlockBox(ServerWorld world, Feature<?> feature, BlockBox box) {
        LandmarkType landmark = LandmarkType.findByFeature(feature);
        if (landmark != null) {
            if (featureBlockBoxes == null) {
                featureBlockBoxes = new HashMap<>();
            }
            featureBlockBoxes
                .computeIfAbsent(feature, k -> new ArrayList<>())
                .add(box);
            AchieveToDoMod.getServer().onLandmarkLoadedStatusChanged(world, pos, landmark, box, true);
        }
        markNeedsSaving();
    }

    @Shadow
    @Final
    protected ChunkPos pos;

    @Shadow
    public abstract void markNeedsSaving();
}
