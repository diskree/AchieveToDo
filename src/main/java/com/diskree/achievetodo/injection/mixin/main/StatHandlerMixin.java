package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.tracking.TrackedStatType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StatHandler.class)
public class StatHandlerMixin {

    @Inject(
        method = "setStat",
        at = @At("RETURN")
    )
    private void trackStatChange(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            List<TrackedStatType> trackedStatTypes = TrackedStatType.findByStat(stat);
            if (trackedStatTypes != null) {
                for (TrackedStatType trackedStatType : trackedStatTypes) {
                    AchieveToDoMod.getServer().setStat(serverPlayer, trackedStatType, value);
                }
            }
        }
    }
}
