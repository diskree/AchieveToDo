package com.diskree.achievetodo.injection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public interface UsableItemOnBlock {

    boolean achievetodo$canUse(PlayerEntity player, ItemStack stack, Hand hand, BlockHitResult hit);
}
