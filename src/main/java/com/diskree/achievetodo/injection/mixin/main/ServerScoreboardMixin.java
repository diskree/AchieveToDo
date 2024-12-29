package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.scoreboard.ServerScoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {

    @Inject(
        method = "setObjectiveSlot",
        at = @At("RETURN")
    )
    private void trackScoreboardChanged(CallbackInfo ci) {
        ServerScoreboard scoreboard = (ServerScoreboard) (Object) this;
        AchieveToDoMod.getServer().prepareScoreboard(scoreboard);
    }
}
