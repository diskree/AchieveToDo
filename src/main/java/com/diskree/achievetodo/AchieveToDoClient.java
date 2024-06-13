package com.diskree.achievetodo;

import com.diskree.achievetodo.gui.CreateWorldTab;
import com.diskree.achievetodo.networking.ScoreSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AchieveToDoClient implements ClientModInitializer {

    public static int advancementsCount;
    public static CreateWorldTab createWorldTab;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ScoreSyncPayload.ID, (payload, context) ->
            context.client().execute(() ->
                advancementsCount = payload.score()
            )
        );
    }
}
