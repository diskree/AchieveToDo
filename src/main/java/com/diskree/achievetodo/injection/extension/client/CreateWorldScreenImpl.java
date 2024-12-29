package com.diskree.achievetodo.injection.extension.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface CreateWorldScreenImpl {

    boolean achievetodo$isWaitingDatapack();

    void achievetodo$setWaitingDatapack(boolean isWaitingDatapack);

}
