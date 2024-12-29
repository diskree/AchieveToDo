package com.diskree.achievetodo.client;

import com.diskree.achievetodo.BuildConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public enum InternalPack {

    BACAP_OVERRIDE,
    BACAP_HARDCORE_OVERRIDE,
    BACAP_TERRALITH_OVERRIDE,
    BACAP_AMPLIFIED_NETHER_OVERRIDE,
    BACAP_NULLSCAPE_OVERRIDE,
    BACAP_REWARDS_ITEM,
    BACAP_REWARDS_EXPERIENCE,
    BACAP_REWARDS_TROPHY,
    BACAP_COOPERATIVE_MODE;

    public @NotNull String getDatapackName() {
        return Identifier.of(BuildConfig.MOD_ID, getLowerCaseName()).toString();
    }

    private @NotNull String getLowerCaseName() {
        return name().toLowerCase();
    }
}
