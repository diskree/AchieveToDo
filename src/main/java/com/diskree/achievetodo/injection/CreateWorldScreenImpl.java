package com.diskree.achievetodo.injection;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface CreateWorldScreenImpl {
    boolean achievetodo$datapacksLoaded();
}
