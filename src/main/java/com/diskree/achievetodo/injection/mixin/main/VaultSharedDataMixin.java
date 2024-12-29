package com.diskree.achievetodo.injection.mixin.main;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.AchieveToDoMod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(VaultSharedData.class)
public class VaultSharedDataMixin {

    @WrapOperation(
        method = "updateConnectedPlayers",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"
        )
    )
    private Stream<UUID> excludePlayersWithoutUnlockVaultAbility(
        Stream<UUID> stream,
        Predicate<? super UUID> predicate,
        @NotNull Operation<Stream<UUID>> original,
        @Local(argsOnly = true) ServerWorld world
    ) {
        return original.call(stream, predicate).filter(
            uuid -> {
                PlayerEntity player = world.getPlayerByUuid(uuid);
                return player != null && !AchieveToDoMod.isAbilityLocked(player, AbilityType.UNLOCK_VAULT, true);
            }
        );
    }
}
