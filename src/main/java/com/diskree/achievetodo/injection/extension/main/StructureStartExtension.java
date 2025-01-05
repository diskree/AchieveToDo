package com.diskree.achievetodo.injection.extension.main;

import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.util.math.BlockBox;

public interface StructureStartExtension {
    void achievetodo$setLandmarkType(LandmarkType landmarkType);

    LandmarkType achievetodo$getLandmarkType();

    void achievetodo$setLandmarkBlockBox(BlockBox blockBox);

    BlockBox achievetodo$getLandmarkBlockBox();
}
