package com.diskree.achievetodo;

import com.diskree.achievetodo.networking.SyncAdvancementsCountPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class AchieveToDoClient implements ClientModInitializer {

    public static int obtainedAdvancementsCount = -1;

    public static boolean isNotReady() {
        return obtainedAdvancementsCount == -1;
    }

    @Override
    public void onInitializeClient() {
        registerInternalDataPacks();

        ClientPlayNetworking.registerGlobalReceiver(SyncAdvancementsCountPayload.ID, (payload, context) ->
            context.client().execute(() -> obtainedAdvancementsCount = payload.count())
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
