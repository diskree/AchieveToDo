package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Shadow
    private boolean autoJumpEnabled;

    @Inject(
        method = "isAutoJumpEnabled",
        at = @At("HEAD"),
        cancellable = true
    )
    private void blockJump(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity) (Object) this;
        if (autoJumpEnabled && AchieveToDo.isActionBlocked(clientPlayerEntity, BlockedActionType.JUMP, true)) {
            cir.setReturnValue(false);
        }
    }
}
