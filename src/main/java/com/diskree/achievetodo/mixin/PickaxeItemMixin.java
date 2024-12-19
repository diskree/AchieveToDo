package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.PickaxeItemImpl;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PickaxeItem.class)
public abstract class PickaxeItemMixin implements PickaxeItemImpl {

    @Unique
    private ToolMaterial material;

    @Override
    public ToolMaterial achievetodo$getMaterial() {
        return material;
    }

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void saveMaterial(
        ToolMaterial material,
        float attackDamage,
        float attackSpeed,
        Item.Settings settings,
        CallbackInfo ci
    ) {
        this.material = material;
    }
}
