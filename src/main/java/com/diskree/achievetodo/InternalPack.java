package com.diskree.achievetodo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public enum InternalPack {

    BACAP_OVERRIDE,
    BACAP_HARDCORE_OVERRIDE,
    BACAP_REWARDS_ITEM,
    BACAP_REWARDS_EXPERIENCE,
    BACAP_REWARDS_TROPHY,
    BACAP_COOPERATIVE_MODE;

    public @NotNull String getDatapackName() {
        return Identifier.of(BuildConfig.MOD_ID, name().toLowerCase()).toString();
    }
}
