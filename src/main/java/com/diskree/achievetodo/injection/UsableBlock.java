package com.diskree.achievetodo.injection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public interface UsableBlock {

    boolean achievetodo$canUse(PlayerEntity player, Hand hand, BlockHitResult hit);
}
