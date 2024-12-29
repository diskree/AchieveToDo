package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.AchieveToDoMod;
import com.diskree.achievetodo.server.AdvancementsMode;
import com.diskree.achievetodo.tracking.TrackedScoreType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.scoreboard.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {

    @Mixin(targets = "net/minecraft/scoreboard/Scoreboard$1")
    public static class ScoreMixin {

        @WrapOperation(
            method = "update",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/scoreboard/Scoreboard;updateScore(Lnet/minecraft/scoreboard/ScoreHolder;Lnet/minecraft/scoreboard/ScoreboardObjective;Lnet/minecraft/scoreboard/ScoreboardScore;)V"
            )
        )
        private void trackScoreChanged(
            Scoreboard scoreboard,
            @NotNull ScoreHolder scoreHolder,
            @NotNull ScoreboardObjective objective,
            ScoreboardScore scoreboardScore,
            @NotNull Operation<Void> original
        ) {
            original.call(scoreboard, scoreHolder, objective, scoreboardScore);

            if (scoreboard instanceof ServerScoreboard serverScoreboard) {
                int score = scoreboardScore.getScore();
                String objectiveName = objective.getName();
                AdvancementsMode advancementsMode = AchieveToDoMod.getServer().currentAdvancementsMode;
                if (advancementsMode != null &&
                    advancementsMode == AdvancementsMode.findByObjectiveName(objectiveName)
                ) {
                    if (advancementsMode.isTeamsMode()) {
                        Team team = scoreboard.getScoreHolderTeam(scoreHolder.getNameForScoreboard());
                        if (team != null) {
                            PlayerManager playerManager = serverScoreboard.server.getPlayerManager();
                            for (String playerName : team.getPlayerList()) {
                                ServerPlayerEntity serverPlayer = playerManager.getPlayer(playerName);
                                if (serverPlayer != null) {
                                    AchieveToDoMod.getServer().setObtainedAdvancementsCount(serverPlayer, score);
                                }
                            }
                        }
                    } else if (scoreHolder instanceof ServerPlayerEntity serverPlayer) {
                        AchieveToDoMod.getServer().setObtainedAdvancementsCount(serverPlayer, score);
                    }
                } else if (scoreHolder instanceof ServerPlayerEntity serverPlayer) {
                    List<TrackedScoreType> progressTypes = TrackedScoreType.findByObjectiveName(objectiveName);
                    if (progressTypes != null) {
                        for (TrackedScoreType progressType : progressTypes) {
                            AchieveToDoMod.getServer().setScore(
                                serverPlayer,
                                progressType,
                                progressType.fixScore(scoreboard, scoreHolder, score)
                            );
                        }
                    }
                }
            }
        }
    }
}
