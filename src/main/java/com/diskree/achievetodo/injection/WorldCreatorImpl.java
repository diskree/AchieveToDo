package com.diskree.achievetodo.injection;

import com.diskree.achievetodo.config.Configuration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface WorldCreatorImpl {

    Configuration achievetodo$getConfiguration();

    void achievetodo$setConfiguration(Configuration configuration);
}
