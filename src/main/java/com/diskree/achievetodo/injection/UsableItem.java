package com.diskree.achievetodo.injection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;

public interface UsableItem {

    boolean achievetodo$canUse(PlayerEntity player, BlockHitResult hit);
}
