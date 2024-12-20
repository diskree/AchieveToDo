package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import net.minecraft.client.tutorial.FindTreeTutorialStepHandler;
import net.minecraft.client.tutorial.TutorialManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FindTreeTutorialStepHandler.class)
public class FindTreeTutorialStepHandlerMixin {

    @Shadow
    @Final
    private TutorialManager manager;

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
        if (AchieveToDo.isAbilityLocked(manager.getClient().player, AbilityType.BREAK_BLOCKS, true)) {
            ci.cancel();
        }
    }

    @ModifyConstant(
        method = "tick",
        constant = @Constant(intValue = 6000)
    )
    public int rfgfg(int constant) {
        return 300;
    }

}
