package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.ability.LandmarkType;
import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import com.diskree.achievetodo.injection.extension.main.FeatureGenerationTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Feature.class)
public class FeatureMixin {

    @WrapOperation(
        method = "generateIfValid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/Feature;generate(Lnet/minecraft/world/gen/feature/util/FeatureContext;)Z"
        )
    )
    public <FC extends FeatureConfig> boolean getFeatureBlockBox(
        Feature<?> feature,
        FeatureContext<FC> featureContext,
        @NotNull Operation<Boolean> original,
        @Local(argsOnly = true) StructureWorldAccess world,
        @Local(argsOnly = true) BlockPos pos
    ) {
        boolean shouldSave = LandmarkType.FEATURES.contains(feature);
        if (shouldSave && world instanceof FeatureGenerationTracker featureGenerationTracker) {
            featureGenerationTracker.achievetodo$setTrackFeatureGeneration(true);
        }
        boolean result = original.call(feature, featureContext);
        if (shouldSave) {
            Chunk chunk = world.getChunk(pos);
            if (chunk instanceof ChunkExtension chunkExtension &&
                world instanceof FeatureGenerationTracker featureGenerationTracker
            ) {
                BlockBox featureBlockBox = featureGenerationTracker.achievetodo$getFeatureBlockBox();
                if (featureBlockBox != null) {
                    chunkExtension.achievetodo$addFeatureBlockBox(world.toServerWorld(), feature, featureBlockBox);
                }
                featureGenerationTracker.achievetodo$setTrackFeatureGeneration(false);
            }
        }
        return result;
    }
}
