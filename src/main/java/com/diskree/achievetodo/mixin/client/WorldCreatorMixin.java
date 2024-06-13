package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.config.Configuration;
import com.diskree.achievetodo.injection.WorldCreatorImpl;
import net.minecraft.client.gui.screen.world.WorldCreator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldCreator.class)
public class WorldCreatorMixin implements WorldCreatorImpl {

    @Unique
    private Configuration configuration;

    @Override
    public Configuration achievetodo$getConfiguration() {
        return configuration;
    }

    @Override
    public void achievetodo$setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
