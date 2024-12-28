package com.diskree.achievetodo;

import com.diskree.achievetodo.networking.SyncAdvancementsCountPayload;
import com.diskree.achievetodo.networking.SyncScorePayload;
import com.diskree.achievetodo.networking.SyncStatPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchieveToDoClient implements ClientModInitializer {

    private static int obtainedAdvancementsCount = -1;
    private static final Map<TrackedScoreType, Integer> trackedScores = new HashMap<>();
    private static final Map<TrackedStatType, Integer> trackedStats = new HashMap<>();

    public static boolean isNotReady() {
        return obtainedAdvancementsCount == -1;
    }

    public static int getObtainedAdvancementsCount() {
        return obtainedAdvancementsCount;
    }

    public static int getTrackedScore(@NotNull TrackedScoreType progressType) {
        return trackedScores.getOrDefault(progressType, 0);
    }

    public static int getTrackedStat(@NotNull TrackedStatType statType) {
        return trackedStats.getOrDefault(statType, 0);
    }

    public static int getTrackedNearbyEntitiesCount(TrackedNearbyEntitiesType type) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isSpectator()) {
            return 0;
        }
        int radius = type.getRadius();
        Vec3d playerPos = player.getPos();
        Box area = new Box(playerPos, playerPos).expand(radius);
        List<EntityType<?>> trackedEntities = type.getEntities();
        boolean isBabySeparated = type.isBabySeparated();
        Map<EntityType<?>, Map<Boolean, Boolean>> trackedMap = new HashMap<>();
        for (EntityType<?> entityType : trackedEntities) {
            Map<Boolean, Boolean> babyAndAdultMap = new HashMap<>();
            babyAndAdultMap.put(false, false);
            if (isBabySeparated) {
                babyAndAdultMap.put(true, false);
            }
            trackedMap.put(entityType, babyAndAdultMap);
        }
        int entitiesCount = 0;
        List<Entity> entities = player.getWorld().getEntitiesByType(
            TypeFilter.instanceOf(Entity.class),
            area,
            entity -> trackedEntities.contains(entity.getType())
        );
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (playerPos.distanceTo(entity.getPos()) > radius) {
                    continue;
                }
                Map<Boolean, Boolean> stateMap = trackedMap.get(entity.getType());
                if (stateMap != null) {
                    boolean isBaby = isBabySeparated && livingEntity.isBaby();
                    if (!stateMap.getOrDefault(isBaby, false)) {
                        stateMap.put(isBaby, true);
                        entitiesCount++;
                    }
                }
            }
        }
        return entitiesCount;
    }

    @Override
    public void onInitializeClient() {
        registerInternalDataPacks();

        ClientPlayNetworking.registerGlobalReceiver(SyncAdvancementsCountPayload.ID, (payload, context) ->
            context.client().execute(() -> obtainedAdvancementsCount = payload.count())
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncScorePayload.ID, (payload, context) ->
            context.client().execute(() -> trackedScores.put(payload.progressType(), payload.progress()))
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncStatPayload.ID, (payload, context) ->
            context.client().execute(() -> trackedStats.put(payload.statType(), payload.progress()))
        );

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> obtainedAdvancementsCount = -1);
    }

    private void registerInternalDataPacks() {
        FabricLoader.getInstance().getModContainer(BuildConfig.MOD_ID).ifPresent(modContainer -> {
            for (InternalPack internalPack : InternalPack.values()) {
                ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.of(internalPack.getDatapackName()),
                    modContainer,
                    ResourcePackActivationType.NORMAL
                );
            }
        });
    }
}
