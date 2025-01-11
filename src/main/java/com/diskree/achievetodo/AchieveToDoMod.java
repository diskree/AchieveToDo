package com.diskree.achievetodo;

import com.diskree.achievetodo.ability.AbilityType;
import com.diskree.achievetodo.ability.DimensionType;
import com.diskree.achievetodo.client.AchieveToDoClient;
import com.diskree.achievetodo.client.gui.AdvancementsTabType;
import com.diskree.achievetodo.networking.c2s.DemystifyAbilityTypePayload;
import com.diskree.achievetodo.networking.c2s.DemystifyRandomAdvancementCriterionPayload;
import com.diskree.achievetodo.networking.s2c.*;
import com.diskree.achievetodo.server.AchieveToDoServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AchieveToDoMod implements ModInitializer {

    public static Logger logger = LoggerFactory.getLogger(BuildConfig.MOD_NAME);

    private static final List<String> TRACKER_BLACK_LIST = List.of(
        "blazeandcave:redstone/take_notes",
        "blazeandcave:nether/this_ones_mine",
        "blazeandcave:challenges/riddle_me_this",
        "blazeandcave:challenges/were_in_the_endgame_now"
    );
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

    public static @NotNull Identifier getIdentifier(String path) {
        return Identifier.of(BuildConfig.MOD_ID, path);
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

    public static boolean isTrackableAdvancement(@NotNull AdvancementEntry advancementEntry) {
        Advancement advancement = advancementEntry.value();
        AdvancementDisplay display = advancement.display().orElse(null);
        if (advancement.isRoot() ||
            display == null ||
            display.isHidden() ||
            advancement.requirements().requirements().size() <= 1 ||
            TRACKER_BLACK_LIST.contains(advancementEntry.id().toString())
        ) {
            return false;
        }
        AdvancementsTabType tab = AdvancementsTabType.findByAdvancement(advancementEntry);
        return tab != null && tab != AdvancementsTabType.ABILITIES && tab != AdvancementsTabType.BACAP;
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(
            DemystifyAbilityTypePayload.ID,
            DemystifyAbilityTypePayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
            DemystifyRandomAdvancementCriterionPayload.ID,
            DemystifyRandomAdvancementCriterionPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
            AbilitiesConfigurationLoadedPayload.ID,
            AbilitiesConfigurationLoadedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            ObtainedAdvancementsCountChangedPayload.ID,
            ObtainedAdvancementsCountChangedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            LandmarksLockedStatusChangedPayload.ID,
            LandmarksLockedStatusChangedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            NotifyLandmarkTypesUnlockedPayload.ID,
            NotifyLandmarkTypesUnlockedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            LockedLandmarkResizedPayload.ID,
            LockedLandmarkResizedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            NotifyScoreProgressChangedPayload.ID,
            NotifyScoreProgressChangedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            NotifyStatProgressChangedPayload.ID,
            NotifyStatProgressChangedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            DemystifiedCriteriaLoadedPayload.ID,
            DemystifiedCriteriaLoadedPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
            NotifyAdvancementCriterionDemystifiedPayload.ID,
            NotifyAdvancementCriterionDemystifiedPayload.CODEC
        );
    }
}
