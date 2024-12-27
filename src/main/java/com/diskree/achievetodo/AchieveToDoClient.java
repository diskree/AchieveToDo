package com.diskree.achievetodo;

import com.diskree.achievetodo.networking.SyncAdvancementsCountPayload;
import com.diskree.achievetodo.networking.SyncDynamicProgressPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AchieveToDoClient implements ClientModInitializer {

    private static int obtainedAdvancementsCount = -1;
    private static final Map<DynamicProgressType, Integer> dynamicProgresses = new HashMap<>();

    public static boolean isNotReady() {
        return obtainedAdvancementsCount == -1;
    }

    public static int getObtainedAdvancementsCount() {
        return obtainedAdvancementsCount;
    }

    public static int getDynamicProgress(DynamicProgressType progressType) {
        return dynamicProgresses.getOrDefault(progressType, 0);
    }

    @Override
    public void onInitializeClient() {
        registerInternalDataPacks();

        ClientPlayNetworking.registerGlobalReceiver(SyncAdvancementsCountPayload.ID, (payload, context) ->
            context.client().execute(() -> obtainedAdvancementsCount = payload.count())
        );
        ClientPlayNetworking.registerGlobalReceiver(SyncDynamicProgressPayload.ID, (payload, context) ->
            context.client().execute(() -> dynamicProgresses.put(payload.progressType(), payload.progress()))
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
