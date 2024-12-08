package com.diskree.achievetodo;

import com.diskree.achievetodo.gui.CreateWorldTab;
import com.diskree.achievetodo.networking.ScoreSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class AchieveToDoClient implements ClientModInitializer {

    public static int advancementsCount;
    public static CreateWorldTab createWorldTab;

    @Override
    public void onInitializeClient() {
        registerInternalDataPacks();

        ClientPlayNetworking.registerGlobalReceiver(ScoreSyncPayload.ID, (payload, context) ->
            context.client().execute(() -> advancementsCount = payload.score())
        );
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
