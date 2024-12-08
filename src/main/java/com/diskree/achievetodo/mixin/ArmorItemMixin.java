package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.injection.ArmorItemImpl;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorItem.class)
public class ArmorItemMixin implements ArmorItemImpl {

    @Unique
    private ArmorMaterial material;

    @Unique
    private EquipmentType equipmentType;

    @Override
    public ArmorMaterial achievetodo$getMaterial() {
        return material;
    }

    @Override
    public EquipmentType achievetodo$getEquipmentType() {
        return equipmentType;
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void saveMaterial(ArmorMaterial material, EquipmentType type, Item.Settings settings, CallbackInfo ci) {
        this.material = material;
        this.equipmentType = type;
    }
}
