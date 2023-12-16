package com.diskree.achievetodo.client;

import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.advancements.hints.AncientCityPortalExperienceOrbEntityRenderer;
import com.diskree.achievetodo.advancements.hints.AncientCityPortalItemDisplayEntityRenderer;
import com.diskree.achievetodo.advancements.hints.AncientCityPortalParticleFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

@Environment(EnvType.CLIENT)
public class AchieveToDoClient implements ClientModInitializer {

    public static final int ADVANCEMENTS_SCREEN_MARGIN = 30;

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(AchieveToDo.ANCIENT_CITY_PORTAL_BLOCK, BlendMode.TRANSLUCENT.blockRenderLayer);
        ModelPredicateProviderRegistry.register(AchieveToDo.ANCIENT_CITY_PORTAL_HINT_ITEM, ModelPredicateProviderRegistry.DAMAGE_ID, ModelPredicateProviderRegistry.DAMAGE_PROVIDER);
        ParticleFactoryRegistry.getInstance().register(AchieveToDo.ANCIENT_CITY_PORTAL_PARTICLES, AncientCityPortalParticleFactory::new);
        EntityRendererRegistry.register(AchieveToDo.ANCIENT_CITY_PORTAL_TAB, AncientCityPortalItemDisplayEntityRenderer::new);
        EntityRendererRegistry.register(AchieveToDo.ANCIENT_CITY_PORTAL_ADVANCEMENT, AncientCityPortalItemDisplayEntityRenderer::new);
        EntityRendererRegistry.register(AchieveToDo.ANCIENT_CITY_PORTAL_HINT, AncientCityPortalItemDisplayEntityRenderer::new);
        EntityRendererRegistry.register(AchieveToDo.ANCIENT_CITY_PORTAL_EXPERIENCE_ORB, AncientCityPortalExperienceOrbEntityRenderer::new);
    }
}
