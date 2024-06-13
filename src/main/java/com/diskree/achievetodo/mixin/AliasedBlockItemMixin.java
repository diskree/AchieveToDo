package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.UsableItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AliasedBlockItem.class)
public class AliasedBlockItemMixin extends BlockItem implements UsableItem {

    public AliasedBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public boolean achievetodo$canUse(@NotNull PlayerEntity player, @NotNull BlockHitResult hit) {
        BlockState aliasedBlockState = getBlock().getDefaultState();
        BlockState blockState = player.getWorld().getBlockState(hit.getBlockPos());
        return !aliasedBlockState.isIn(BlockTags.MAINTAINS_FARMLAND) ||
            !blockState.isOf(Blocks.FARMLAND) ||
            hit.getSide() != Direction.UP;
    }
}
