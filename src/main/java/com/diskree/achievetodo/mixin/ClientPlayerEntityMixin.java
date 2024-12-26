package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow
    @Final
    protected MinecraftClient client;

    @ModifyReturnValue(
        method = "isSneaking",
        at = @At("RETURN")
    )
    public boolean lockSneaking(boolean original) {
        if (!original) {
            return false;
        }
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        return !AchieveToDo.isAbilityLocked(player, AbilityType.SNEAK);
    }
}
