package com.diskree.achievetodo;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.networking.c2s.DemystifyAbilityPayload;
import com.diskree.achievetodo.networking.s2c.SyncAbilitiesConfigurationPayload;
import com.diskree.achievetodo.networking.s2c.SyncAdvancementsCountPayload;
import com.diskree.achievetodo.networking.s2c.SyncScorePayload;
import com.diskree.achievetodo.networking.s2c.SyncStatPayload;
import com.diskree.achievetodo.server.AchieveToDoServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AchieveToDoMod implements ModInitializer {

    public static Logger logger = LoggerFactory.getLogger(BuildConfig.MOD_NAME);

    private static AchieveToDoServer server;

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(DemystifyAbilityPayload.ID, DemystifyAbilityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(
            SyncAbilitiesConfigurationPayload.ID, SyncAbilitiesConfigurationPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(SyncAdvancementsCountPayload.ID, SyncAdvancementsCountPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncScorePayload.ID, SyncScorePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncStatPayload.ID, SyncStatPayload.CODEC);

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
        if (ability == null || player.isCreative() || player.isSpectator()) {
            return false;
        }
        if (player.getWorld().isClient) {
            if (ability != AbilityType.VISION) {
                System.out.println("isAbilityLocked check on client:" + ability.getLowerCaseName());
            }
            return AchieveToDoClient.isAbilityLocked(ability);
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (ability != AbilityType.VISION) {
                System.out.println("isAbilityLocked check on server:" + ability.getLowerCaseName());
            }
            return server.isAbilityLocked(serverPlayer, ability, checkOnly);
        }
        return true;
    }
}
