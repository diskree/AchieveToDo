package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.injection.extension.main.FeatureGenerationTracker;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRegion.class)
public abstract class ChunkRegionMixin implements StructureWorldAccess, FeatureGenerationTracker {

    @Unique
    private boolean shouldTrackFeatureGeneration;

    @Unique
    private BlockBox featureBlockBox;

    @Override
    public void achievetodo$setTrackFeatureGeneration(boolean shouldTrackFeatureGeneration) {
        if (this.shouldTrackFeatureGeneration == shouldTrackFeatureGeneration) {
            return;
        }
        this.shouldTrackFeatureGeneration = shouldTrackFeatureGeneration;
        featureBlockBox = null;
    }

    @Override
    public BlockBox achievetodo$getFeatureBlockBox() {
        return featureBlockBox;
    }

    @SuppressWarnings("deprecation")
    @Inject(
        method = "setBlockState",
        at = @At(value = "TAIL")
    )
    private void trackFeatureGeneration(
        BlockPos pos,
        BlockState state,
        int flags,
        int maxUpdateDepth,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (shouldTrackFeatureGeneration) {
            if (featureBlockBox == null) {
                featureBlockBox = new BlockBox(pos);
            }
            featureBlockBox.encompass(pos);
        }
    }
}
