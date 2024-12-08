package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.SwordItemImpl;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwordItem.class)
public class SwordItemMixin implements SwordItemImpl {

    @Unique
    private ToolMaterial swordMaterial;

    @Override
    public ToolMaterial achievetodo$getSwordMaterial() {
        return swordMaterial;
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void saveMaterial(
        ToolMaterial material,
        float attackDamage,
        float attackSpeed,
        Item.Settings settings,
        CallbackInfo ci
    ) {
        this.swordMaterial = material;
    }
}
