package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.injection.extension.main.FeatureGenerationTracker;
import net.minecraft.block.BlockState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements FeatureGenerationTracker {

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
    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        boolean result = super.setBlockState(pos, state, flags, maxUpdateDepth);
        if (result && shouldTrackFeatureGeneration) {
            if (featureBlockBox == null) {
                featureBlockBox = new BlockBox(pos);
            }
            featureBlockBox.encompass(pos);
        }
        return result;
    }

    protected ServerWorldMixin(
        MutableWorldProperties properties,
        RegistryKey<World> registryRef,
        DynamicRegistryManager registryManager,
        RegistryEntry<DimensionType> dimensionEntry,
        boolean isClient,
        boolean debugWorld,
        long seed,
        int maxChainedNeighborUpdates
    ) {
        super(
            properties,
            registryRef,
            registryManager,
            dimensionEntry,
            isClient,
            debugWorld,
            seed,
            maxChainedNeighborUpdates
        );
    }
}
