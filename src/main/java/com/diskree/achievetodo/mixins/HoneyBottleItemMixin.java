package com.diskree.achievetodo.mixins;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBottleItem.class)
public class HoneyBottleItemMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
    private void finishUsingInject(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (stack != null && stack.getItem() != null && stack.getItem().isFood() && user instanceof PlayerEntity && AchieveToDoMod.isFoodBlocked(stack.getItem().getFoodComponent())) {
            MinecraftClient.getInstance().options.useKey.setPressed(false);
            cir.setReturnValue(stack);
        }
    }
}
