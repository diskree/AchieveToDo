package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.injection.CreateWorldScreenImpl;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(
        method = "setScreen",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setScreenInject(Screen screen, CallbackInfo ci) {
        if (screen instanceof CreateWorldScreen createWorldScreen &&
            screen instanceof CreateWorldScreenImpl createWorldScreenImpl &&
            createWorldScreenImpl.achievetodo$isWaitingDatapack()
        ) {
            createWorldScreen.createLevel();
            createWorldScreenImpl.achievetodo$setWaitingDatapack(false);
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "handleInputEvents",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z",
            ordinal = 4
        )
    )
    public boolean lockInventory(KeyBinding keyBinding, @NotNull Operation<Boolean> original) {
        return original.call(keyBinding) && !AchieveToDo.isAbilityLocked(player, AbilityType.OPEN_INVENTORY);
    }
}
