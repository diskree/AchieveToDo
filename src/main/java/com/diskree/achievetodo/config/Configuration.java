package com.diskree.achievetodo.config;

import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import net.minecraft.text.Text;

import java.util.Map;

public record Configuration(
    String presetName,
    Map<BlockedActionType, Integer> counts
) {

    public Text getPresetNameText() {
        return Text.literal(presetName);
    }
}
