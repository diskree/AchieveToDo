package com.diskree.achievetodo;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.networking.c2s.DemystifyAbilityPayload;
import com.diskree.achievetodo.networking.s2c.*;
import com.diskree.achievetodo.server.AchieveToDoServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AchieveToDoMod implements ModInitializer {

    public static Logger logger = LoggerFactory.getLogger(BuildConfig.MOD_NAME);

    private static AchieveToDoServer server;

    @Override
    public void onInitialize() {
        registerPayloads();

        server = new AchieveToDoServer();
        server.onInitializeServer();
    }

    public static AchieveToDoServer getServer() {
        return server;
    }

    public static boolean isAbilityLocked(@NotNull PlayerEntity player, AbilityType ability) {
        return isAbilityLocked(player, ability, false);
    }

    public static boolean isAbilityLocked(@NotNull PlayerEntity player, AbilityType ability, boolean checkOnly) {
        if (player.getWorld().isClient) {
            return AchieveToDoClient.isAbilityLocked(ability, checkOnly);
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            return server.isAbilityLocked(serverPlayer, ability, checkOnly);
        }
        return true;
    }

    public static boolean isTargetInLockedLandmark(@NotNull PlayerEntity actor, @NotNull Entity target) {
        return isTargetInLockedLandmark(actor, target.getWorld(), target.getBoundingBox());
    }

    public static boolean isTargetInLockedLandmark(
        @NotNull PlayerEntity actor,
        World targetWorld,
        BlockPos targetBlockPos
    ) {
        return isTargetInLockedLandmark(actor, targetWorld, new BlockBox(targetBlockPos));
    }

    public static boolean isTargetInLockedLandmark(
        @NotNull PlayerEntity actor,
        World targetWorld,
        BlockBox targetBlockBox
    ) {
        return isTargetInLockedLandmark(actor, targetWorld, Box.from(targetBlockBox));
    }

    public static boolean isTargetInLockedLandmark(
        @NotNull PlayerEntity actor,
        @NotNull World targetWorld,
        Box targetBox
    ) {
        DimensionType targetDimensionType = DimensionType.findByWorld(targetWorld.getRegistryKey());
        if (targetDimensionType == null) {
            return false;
        }
        if (actor.getWorld().isClient) {
            return AchieveToDoClient.isInLockedLandmark(targetDimensionType, targetBox);
        }
        if (actor instanceof ServerPlayerEntity serverPlayer) {
            return server.isInLockedLandmark(serverPlayer, targetDimensionType, targetBox);
        }
        return true;
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(DemystifyAbilityPayload.ID, DemystifyAbilityPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(
            SyncAbilitiesConfigurationPayload.ID,
            SyncAbilitiesConfigurationPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            SyncAdvancementsCountPayload.ID,
            SyncAdvancementsCountPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            SyncLockedLandmarksPayload.ID,
            SyncLockedLandmarksPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            SyncLandmarkTypesUnlockedPayload.ID,
            SyncLandmarkTypesUnlockedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            SyncResizedLandmarkPayload.ID,
            SyncResizedLandmarkPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(SyncScorePayload.ID, SyncScorePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncStatPayload.ID, SyncStatPayload.CODEC);
    }

}
