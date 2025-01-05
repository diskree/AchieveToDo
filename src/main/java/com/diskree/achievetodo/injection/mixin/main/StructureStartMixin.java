package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.ability.DimensionalBlockBox;
import com.diskree.achievetodo.ability.LandmarkType;
import com.diskree.achievetodo.injection.extension.main.LandmarkGenerationTracker;
import com.diskree.achievetodo.injection.extension.main.StructureStartExtension;
import com.diskree.achievetodo.server.Constants;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureStart.class)
public class StructureStartMixin implements StructureStartExtension {

    @Unique
    private LandmarkType landmarkType;

    @Unique
    private BlockBox landmarkBlockBox;

    @Override
    public void achievetodo$setLandmarkType(LandmarkType landmarkType) {
        this.landmarkType = landmarkType;
    }

    @Override
    public LandmarkType achievetodo$getLandmarkType() {
        return landmarkType;
    }

    @Override
    public void achievetodo$setLandmarkBlockBox(BlockBox landmarkBlockBox) {
        this.landmarkBlockBox = landmarkBlockBox;
    }

    @Override
    public BlockBox achievetodo$getLandmarkBlockBox() {
        return landmarkBlockBox;
    }

    @ModifyReturnValue(
        method = "fromNbt",
        at = @At(
            value = "RETURN",
            ordinal = 2
        )
    )
    private static @NotNull StructureStart readLandmarkBlockBoxFromNbt(
        StructureStart original,
        @Local(argsOnly = true) @NotNull NbtCompound nbt
    ) {
        NbtCompound landmarkNbt = nbt.getCompound(Constants.NbtKey.STRUCTURE_LANDMARK);
        if (!landmarkNbt.isEmpty() && original instanceof StructureStartExtension structureStartExtension) {
            structureStartExtension.achievetodo$setLandmarkType(
                LandmarkType.findByName(landmarkNbt.getString(Constants.NbtKey.LANDMARK_TYPE))
            );
            structureStartExtension.achievetodo$setLandmarkBlockBox(
                new BlockBox(
                    landmarkNbt.getInt(Constants.NbtKey.BLOCK_BOX_MIN_X),
                    landmarkNbt.getInt(Constants.NbtKey.BLOCK_BOX_MIN_Y),
                    landmarkNbt.getInt(Constants.NbtKey.BLOCK_BOX_MIN_Z),
                    landmarkNbt.getInt(Constants.NbtKey.BLOCK_BOX_MAX_X),
                    landmarkNbt.getInt(Constants.NbtKey.BLOCK_BOX_MAX_Y),
                    landmarkNbt.getInt(Constants.NbtKey.BLOCK_BOX_MAX_Z)
                )
            );
        }
        return original;
    }

    @Inject(
        method = "toNbt",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putInt(Ljava/lang/String;I)V", ordinal = 0, shift = At.Shift.AFTER)
    )
    private void writeLandmarkBlockBoxToNbt(StructureContext context, ChunkPos chunkPos, CallbackInfoReturnable<NbtCompound> cir, @Local NbtCompound nbtCompound) {
        if (landmarkType != null && landmarkBlockBox != null) {
            NbtCompound landmarkNbt = new NbtCompound();
            landmarkNbt.putString(Constants.NbtKey.LANDMARK_TYPE, landmarkType.getName());
            landmarkNbt.putInt(Constants.NbtKey.BLOCK_BOX_MIN_X, landmarkBlockBox.getMinX());
            landmarkNbt.putInt(Constants.NbtKey.BLOCK_BOX_MIN_Y, landmarkBlockBox.getMinY());
            landmarkNbt.putInt(Constants.NbtKey.BLOCK_BOX_MIN_Z, landmarkBlockBox.getMinZ());
            landmarkNbt.putInt(Constants.NbtKey.BLOCK_BOX_MAX_X, landmarkBlockBox.getMaxX());
            landmarkNbt.putInt(Constants.NbtKey.BLOCK_BOX_MAX_Y, landmarkBlockBox.getMaxY());
            landmarkNbt.putInt(Constants.NbtKey.BLOCK_BOX_MAX_Z, landmarkBlockBox.getMaxZ());
            nbtCompound.put(Constants.NbtKey.STRUCTURE_LANDMARK, landmarkNbt);
        }
    }

    @WrapOperation(
        method = "place",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/structure/StructurePiece;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)V"
        )
    )
    private void trackStructureGeneration(
        StructurePiece structurePiece,
        @NotNull StructureWorldAccess world,
        StructureAccessor structureAccessor,
        ChunkGenerator chunkGenerator,
        Random random,
        BlockBox blockBox,
        ChunkPos chunkPos,
        BlockPos blockPos,
        Operation<Void> original
    ) {
        DimensionType dimensionType = null;
        ServerWorld serverWorld = null;
        LandmarkGenerationTracker tracker = null;
        if (landmarkType != null) {
            serverWorld = world.toServerWorld();
            dimensionType = DimensionType.findByWorld(serverWorld.getRegistryKey());
            if (dimensionType == null) {
                serverWorld = null;
            } else if (world instanceof LandmarkGenerationTracker landmarkGenerationTracker) {
                tracker = landmarkGenerationTracker;
                tracker.achievetodo$setLandmarkGenerationTrackingEnabled(true);
                tracker.achievetodo$setLandmarkBlockBox(landmarkBlockBox);
            }
        }
        original.call(structurePiece, world, structureAccessor, chunkGenerator, random, blockBox, chunkPos, blockPos);
        if (tracker != null) {
            BlockBox newLandmarkBlockBox = tracker.achievetodo$getLandmarkBlockBox();
            if (newLandmarkBlockBox != null) {
                if (landmarkBlockBox != null && !newLandmarkBlockBox.equals(landmarkBlockBox)) {
                    AchieveToDoMod.getServer().onLandmarkDimensionalBlockBoxChanged(
                        serverWorld,
                        pos,
                        landmarkType,
                        new DimensionalBlockBox(dimensionType, landmarkBlockBox),
                        new DimensionalBlockBox(dimensionType, newLandmarkBlockBox)
                    );
                }
                landmarkBlockBox = newLandmarkBlockBox;
            }
            tracker.achievetodo$setLandmarkGenerationTrackingEnabled(false);
            tracker.achievetodo$setLandmarkBlockBox(null);
        }
    }

    @Shadow
    @Final
    private ChunkPos pos;
}
