package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.ability.DungeonType;
import com.diskree.achievetodo.injection.extension.main.ChunkExtension;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.NotNull;
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
            DungeonType dungeon = DungeonType.findByFeature(featureBlockBoxEntry.getKey());
            if (dungeon != null) {
                for (BlockBox blockBox : featureBlockBoxEntry.getValue()) {
                    AchieveToDoMod.getServer().addDungeon(world, dungeon, blockBox);
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
        DungeonType dungeon = DungeonType.findByFeature(feature);
        if (dungeon != null) {
            if (featureBlockBoxes == null) {
                featureBlockBoxes = new HashMap<>();
            }
            featureBlockBoxes.computeIfAbsent(feature, k -> new ArrayList<>()).add(box);
            AchieveToDoMod.getServer().addDungeon(world, dungeon, box);
        }
        markNeedsSaving();
    }

    @Shadow
    public abstract void markNeedsSaving();
}
