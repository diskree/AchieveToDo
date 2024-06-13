package com.diskree.achievetodo.api;

import com.diskree.achievetodo.AchieveToDoClient;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;

import java.util.Map;

public interface AchieveToDoAddon {

    String getName();

    Map<BlockedActionType, Integer> getPreset();

    void onPresetEnabled(boolean isEnabled);

    static void enablePreset(String name) {
        if (AchieveToDoClient.createWorldTab != null) {
            AchieveToDoClient.createWorldTab.enablePreset(name);
        }
    }
}
