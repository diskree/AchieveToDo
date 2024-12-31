package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/WorldChunk;setStructureReferences(Ljava/util/Map;)V",
            shift = At.Shift.AFTER
        )
    )
    public void set(ServerWorld world, ProtoChunk protoChunk, WorldChunk.EntityLoader entityLoader, CallbackInfo ci) {
        WorldChunk worldChunk = (WorldChunk) (Object) this;
        if (worldChunk instanceof ChunkExtension worldChunkExtension &&
            protoChunk instanceof ChunkExtension protoChunkExtension
        ) {
            Map<Feature<?>, List<BlockBox>> featureBlockBoxes = protoChunkExtension.achievetodo$getFeatureBlockBoxes();
            if (featureBlockBoxes != null) {
                worldChunkExtension.achievetodo$setFeatureBlockBoxes(world, featureBlockBoxes);
            }
        }
    }
}
