package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.tutorial.PunchTreeTutorialStepHandler;
import net.minecraft.client.tutorial.TutorialManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PunchTreeTutorialStepHandler.class)
public class PunchTreeTutorialStepHandlerMixin {

    @Shadow
    @Final
    private TutorialManager manager;

    @ModifyConstant(
        method = "tick",
        constant = @Constant(intValue = 600)
    )
    private int showToastFaster(int constant) {
        return 50;
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/tutorial/TutorialManager;getClient()Lnet/minecraft/client/MinecraftClient;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void scheduleUntilBlockBreakingAbilityUnlocked(CallbackInfo ci) {
        ClientPlayerEntity player = manager.getClient().player;
        if (player != null && AchieveToDo.isAbilityLocked(player, AbilityType.BREAK_BLOCKS, true)) {
            ci.cancel();
        }
    }
}
