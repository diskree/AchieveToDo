package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.tracking.TrackedStatisticsDataType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(StatHandler.class)
public class StatHandlerMixin {

    @Inject(
        method = "setStat",
        at = @At("RETURN")
    )
    private void trackStatChange(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            Set<TrackedStatisticsDataType> trackedStatisticsDataTypes = TrackedStatisticsDataType.findByStat(stat);
            if (trackedStatisticsDataTypes != null) {
                for (TrackedStatisticsDataType trackedStatisticsDataType : trackedStatisticsDataTypes) {
                    AchieveToDoMod.getServer().setStat(serverPlayer, trackedStatisticsDataType, value);
                }
            }
        }
    }
}
