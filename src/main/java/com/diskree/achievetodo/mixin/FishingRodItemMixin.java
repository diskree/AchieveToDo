package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin {

    @Inject(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V",
            shift = At.Shift.BEFORE,
            ordinal = 1
        ),
        cancellable = true
    )
    public void lockFishingRod(
        World world,
        PlayerEntity player,
        Hand hand,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (AchieveToDo.isAbilityLocked(player, AbilityType.USING_FISHING_ROD)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
