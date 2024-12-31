package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import com.diskree.achievetodo.injection.extension.main.SerializedChunkExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.SerializedChunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.StorageKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(SerializedChunk.class)
public class SerializedChunkMixin implements SerializedChunkExtension {

    @Unique
    private static final String FEATURES_NBT_KEY = BuildConfig.MOD_ID + "_features";

    @Unique
    private static final String FEATURE_BLOCK_BOXES_NBT_KEY = BuildConfig.MOD_ID + "_featureBlockBoxes";

    @Unique
    private static final String FEATURE_START_MIN_X_NBT_KEY = BuildConfig.MOD_ID + "_featureBlockBox_minX";

    @Unique
    private static final String FEATURE_START_MIN_Y_NBT_KEY = BuildConfig.MOD_ID + "_featureBlockBox_minY";

    @Unique
    private static final String FEATURE_START_MIN_Z_NBT_KEY = BuildConfig.MOD_ID + "_featureBlockBox_minZ";

    @Unique
    private static final String FEATURE_START_MAX_X_NBT_KEY = BuildConfig.MOD_ID + "_featureBlockBox_maxX";

    @Unique
    private static final String FEATURE_START_MAX_Y_NBT_KEY = BuildConfig.MOD_ID + "_featureBlockBox_maxY";

    @Unique
    private static final String FEATURE_START_MAX_Z_NBT_KEY = BuildConfig.MOD_ID + "_featureBlockBox_maxZ";

    @Unique
    private NbtCompound featuresNbt;

    @Override
    public void achievetodo$setFeaturesNbt(NbtCompound featuresNbt) {
        this.featuresNbt = featuresNbt;
    }

    @SuppressWarnings("ExtractMethodRecommender")
    @ModifyReturnValue(
        method = "fromChunk",
        at = @At(value = "TAIL")
    )
    private static SerializedChunk writeFeatureBlockBoxesToChunkNbt(
        SerializedChunk original,
        @Local(argsOnly = true) ServerWorld world,
        @Local(argsOnly = true) Chunk chunk
    ) {
        if (original instanceof SerializedChunkExtension serializedChunkExtension &&
            chunk instanceof ChunkExtension chunkExtension
        ) {
            Map<Feature<?>, List<BlockBox>> featureBlockBoxes = chunkExtension.achievetodo$getFeatureBlockBoxes();
            if (featureBlockBoxes != null) {
                NbtCompound featuresNbt = new NbtCompound();
                NbtCompound featureBlockBoxesNbt = new NbtCompound();
                Registry<Feature<?>> registry = world.getRegistryManager().getOrThrow(RegistryKeys.FEATURE);
                for (Feature<?> feature : featureBlockBoxes.keySet()) {
                    Identifier featureId = registry.getId(feature);
                    if (featureId == null) {
                        continue;
                    }
                    List<BlockBox> starts = featureBlockBoxes.get(feature);
                    NbtList startsNbt = new NbtList();
                    for (BlockBox start : starts) {
                        NbtCompound startNbt = new NbtCompound();
                        startNbt.putInt(FEATURE_START_MIN_X_NBT_KEY, start.getMinX());
                        startNbt.putInt(FEATURE_START_MIN_Y_NBT_KEY, start.getMinY());
                        startNbt.putInt(FEATURE_START_MIN_Z_NBT_KEY, start.getMinZ());
                        startNbt.putInt(FEATURE_START_MAX_X_NBT_KEY, start.getMaxX());
                        startNbt.putInt(FEATURE_START_MAX_Y_NBT_KEY, start.getMaxY());
                        startNbt.putInt(FEATURE_START_MAX_Z_NBT_KEY, start.getMaxZ());
                        startsNbt.add(startNbt);
                    }
                    featureBlockBoxesNbt.put(featureId.toString(), startsNbt);
                }
                featuresNbt.put(FEATURE_BLOCK_BOXES_NBT_KEY, featureBlockBoxesNbt);
                serializedChunkExtension.achievetodo$setFeaturesNbt(featuresNbt);
            }
        }
        return original;
    }

    @ModifyReturnValue(
        method = "fromNbt",
        at = @At(value = "TAIL")
    )
    private static SerializedChunk readFeaturesNbt(SerializedChunk original, @Local(argsOnly = true) NbtCompound nbt) {
        if (original instanceof SerializedChunkExtension serializedChunkExtension && nbt.contains(FEATURES_NBT_KEY)) {
            serializedChunkExtension.achievetodo$setFeaturesNbt(nbt.getCompound(FEATURES_NBT_KEY));
        }
        return original;
    }

    @ModifyReturnValue(
        method = "serialize",
        at = @At(value = "TAIL")
    )
    private NbtCompound writeFeaturesNbt(NbtCompound original) {
        if (featuresNbt != null) {
            original.put(FEATURES_NBT_KEY, featuresNbt);
        }
        return original;
    }

    @Inject(
        method = "convert",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;setStructureReferences(Ljava/util/Map;)V",
            shift = At.Shift.AFTER
        )
    )
    private void setFeaturesToChunk(
        ServerWorld world,
        PointOfInterestStorage poiStorage,
        StorageKey key,
        ChunkPos expectedPos,
        CallbackInfoReturnable<ProtoChunk> cir,
        @Local Chunk chunk
    ) {
        if (chunk instanceof ChunkExtension chunkExtension &&
            featuresNbt != null &&
            featuresNbt.contains(FEATURE_BLOCK_BOXES_NBT_KEY)
        ) {
            Registry<Feature<?>> registry = world.getRegistryManager().getOrThrow(RegistryKeys.FEATURE);
            NbtCompound featureBlockBoxesNbt = featuresNbt.getCompound(FEATURE_BLOCK_BOXES_NBT_KEY);
            Map<Feature<?>, List<BlockBox>> featureBlockBoxes = null;
            for (String featureId : featureBlockBoxesNbt.getKeys()) {
                Feature<?> feature = registry.get(Identifier.of(featureId));
                if (feature != null) {
                    NbtList startsNbt = featureBlockBoxesNbt.getList(featureId, NbtElement.COMPOUND_TYPE);
                    for (int i = 0; i < startsNbt.size(); i++) {
                        NbtCompound startNbt = startsNbt.getCompound(i);
                        if (startNbt != null) {
                            if (featureBlockBoxes == null) {
                                featureBlockBoxes = new HashMap<>();
                            }
                            featureBlockBoxes.computeIfAbsent(feature, k -> new ArrayList<>()).add(new BlockBox(
                                startNbt.getInt(FEATURE_START_MIN_X_NBT_KEY),
                                startNbt.getInt(FEATURE_START_MIN_Y_NBT_KEY),
                                startNbt.getInt(FEATURE_START_MIN_Z_NBT_KEY),
                                startNbt.getInt(FEATURE_START_MAX_X_NBT_KEY),
                                startNbt.getInt(FEATURE_START_MAX_Y_NBT_KEY),
                                startNbt.getInt(FEATURE_START_MAX_Z_NBT_KEY)
                            ));
                        }
                    }
                }
            }
            if (featureBlockBoxes != null) {
                chunkExtension.achievetodo$setFeatureBlockBoxes(world, featureBlockBoxes);
            }
        }
    }
}
