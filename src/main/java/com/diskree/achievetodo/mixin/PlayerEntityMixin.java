package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.BlockedAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void jumpInject(CallbackInfo ci) {
        if (!((PlayerEntity) (Object) this).isTouchingWater() && AchieveToDoMod.isActionBlocked(BlockedAction.jump)) {
            ci.cancel();
        }
    }

    @Inject(method = "canPlaceOn", at = @At("HEAD"), cancellable = true)
    public void canPlaceOnInject(BlockPos pos, Direction facing, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && (stack.isOf(Items.WATER_BUCKET) || stack.getItem() instanceof EntityBucketItem) && AchieveToDoMod.isActionBlocked(BlockedAction.using_water_bucket)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canEquip", at = @At("HEAD"), cancellable = true)
    public void canEquipInject(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && AchieveToDoMod.isEquipmentBlocked(stack.getItem())) {
            cir.setReturnValue(false);
        }
    }
}
