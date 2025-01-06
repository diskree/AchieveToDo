package com.diskree.achievetodo.client.gui;

import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.ability.LandmarkType;
import net.minecraft.util.math.Box;

public record LockedLandmarkBox(LandmarkType landmarkType, DimensionType dimensionType, Box box) {
}
