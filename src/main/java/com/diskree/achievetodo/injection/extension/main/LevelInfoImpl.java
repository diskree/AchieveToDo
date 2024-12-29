package com.diskree.achievetodo.injection.extension.main;

import com.diskree.achievetodo.ability.AbilityType;

import java.util.Map;

public interface LevelInfoImpl {

    String achievetodo$getConfigName();

    void achievetodo$setConfigName(String configName);

    Map<AbilityType, Integer> achievetodo$getAbilitiesConfiguration();

}
