package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.MiningToolItemImpl;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin implements MiningToolItemImpl {

    @Unique
    private ToolMaterial toolMaterial;

    @Override
    public ToolMaterial achievetodo$getToolMaterial() {
        return toolMaterial;
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void saveMaterial(
        ToolMaterial material,
        TagKey<Block> effectiveBlocks,
        float attackDamage,
        float attackSpeed,
        Item.Settings settings,
        CallbackInfo ci
    ) {
        this.toolMaterial = material;
    }
}
