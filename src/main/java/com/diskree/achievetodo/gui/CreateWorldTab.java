package com.diskree.achievetodo.gui;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.injection.WorldCreatorImpl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class CreateWorldTab extends GridScreenTab {

    private static final Identifier MENU_LIST_BACKGROUND_TEXTURE =
        Identifier.ofVanilla("textures/gui/menu_list_background.png");

    private WorldScreenOptionGrid rewardsSection;
    private WorldScreenOptionGrid customGenerationSection;
    private WorldScreenOptionGrid lanSection;

    private GridWidget rewardsContainer;
    private GridWidget customGenerationContainer;
    private GridWidget lanContainer;

    public CreateWorldTab(CreateWorldScreen screen) {
        super(Text.of(BuildConfig.MOD_NAME));
        if (screen == null || screen.client == null) {
            return;
        }
        WorldCreator worldCreator = screen.getWorldCreator();
        WorldCreatorImpl worldSettings = (WorldCreatorImpl) worldCreator;

        grid.getMainPositioner().alignHorizontalCenter();

        GridWidget.Adder rootContainer = grid.setColumnSpacing(30).createAdder(2);

        GridWidget.Adder rewardsTitleContainer = new GridWidget().createAdder(1);
        rewardsTitleContainer.add(new TextWidget(
            Text.translatable("achievetodo.world_creation_tab.rewards.title").copy()
                .formatted(Formatting.YELLOW),
            screen.client.textRenderer)
        );
        WorldScreenOptionGrid.Builder rewardsSectionBuilder = WorldScreenOptionGrid.builder(130);
        rewardsSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.rewards.items"),
            worldSettings::achievetodo$isItemRewardsEnabled,
            worldSettings::achievetodo$setItemRewardsEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.rewards.items.tooltip"));
        rewardsSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.rewards.experience"),
            worldSettings::achievetodo$isExperienceRewardsEnabled,
            worldSettings::achievetodo$setExperienceRewardsEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.rewards.experience.tooltip"));
        rewardsSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.rewards.trophy"),
            worldSettings::achievetodo$isTrophyRewardsEnabled,
            worldSettings::achievetodo$setTrophyRewardsEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.rewards.trophy.tooltip"));
        rewardsContainer = new GridWidget();
        rewardsContainer.add(rewardsTitleContainer.getGridWidget(), 0, 0, grid.copyPositioner());
        rewardsSection = rewardsSectionBuilder.build();
        rewardsContainer.add(rewardsSection.getLayout(), 0, 0, grid.copyPositioner().marginTop(10));
        rootContainer.add(rewardsContainer, 1, grid.copyPositioner().marginTop(8));

        GridWidget.Adder customGenerationTitleContainer = new GridWidget().createAdder(1);
        customGenerationTitleContainer.add(new TextWidget(
            Text.translatable("achievetodo.world_creation_tab.generation.title").copy()
                .formatted(Formatting.YELLOW),
            screen.client.textRenderer
        ));
        WorldScreenOptionGrid.Builder customGenerationSectionBuilder = WorldScreenOptionGrid.builder(130);
        customGenerationSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.generation.overworld"),
            worldSettings::achievetodo$isTerralithEnabled,
            worldSettings::achievetodo$setTerralithEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.generation.overworld.tooltip"));
        customGenerationSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.generation.nether"),
            worldSettings::achievetodo$isAmplifiedNetherEnabled,
            worldSettings::achievetodo$setAmplifiedNetherEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.generation.nether.tooltip"));
        customGenerationSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.generation.end"),
            worldSettings::achievetodo$isNullscapeEnabled,
            worldSettings::achievetodo$setNullscapeEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.generation.end.tooltip"));
        customGenerationContainer = new GridWidget();
        customGenerationContainer.add(customGenerationTitleContainer.getGridWidget(), 0, 0, grid.copyPositioner());
        customGenerationSection = customGenerationSectionBuilder.build();
        customGenerationContainer.add(customGenerationSection.getLayout(), 0, 0, grid.copyPositioner().marginTop(10));
        rootContainer.add(customGenerationContainer, 1, grid.copyPositioner().marginTop(8));

        GridWidget.Adder lanTitleContainer = new GridWidget().createAdder(1);
        lanTitleContainer.add(new TextWidget(
            Text.translatable("lanServer.title").copy()
                .formatted(Formatting.YELLOW),
            screen.client.textRenderer
        ));
        WorldScreenOptionGrid.Builder lanSectionBuilder = WorldScreenOptionGrid.builder(170);
        lanSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.lan.cooperative_mode"),
            worldSettings::achievetodo$isCooperativeModeEnabled,
            worldSettings::achievetodo$setCooperativeModeEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.lan.cooperative_mode.tooltip"));

        lanContainer = new GridWidget();
        lanContainer.add(lanTitleContainer.getGridWidget(), 0, 0, grid.copyPositioner());
        lanSection = lanSectionBuilder.build();
        lanContainer.add(lanSection.getLayout(), 0, 0, grid.copyPositioner().marginTop(10));
        rootContainer.add(lanContainer, 2, grid.copyPositioner().marginTop(32));

        worldCreator.addListener(creator -> {
            if (rewardsSection != null) {
                rewardsSection.refresh();
            }
            if (customGenerationSection != null) {
                customGenerationSection.refresh();
            }
            if (lanSection != null) {
                lanSection.refresh();
            }
        });
        grid.refreshPositions();
    }

    public void render(DrawContext context) {
        renderContainerBackground(context, rewardsContainer);
        renderContainerBackground(context, customGenerationContainer);
        renderContainerBackground(context, lanContainer);
    }

    private void renderContainerBackground(DrawContext context, GridWidget container) {
        if (container == null) {
            return;
        }
        int padding = 6;
        int lineHeight = 2;
        int textureSize = 32;
        int x = container.getX() - padding;
        int y = container.getY() - padding;
        int width = container.getWidth() + padding * 2;
        int height = container.getHeight() + padding * 2;
        context.drawTexture(
            RenderLayer::getGuiTextured,
            Screen.HEADER_SEPARATOR_TEXTURE,
            x,
            y - lineHeight,
            0.0f,
            0.0f,
            width,
            lineHeight,
            textureSize,
            lineHeight
        );
        context.drawTexture(
            RenderLayer::getGuiTextured,
            MENU_LIST_BACKGROUND_TEXTURE,
            x,
            y,
            x + width,
            y + height,
            width,
            height,
            textureSize,
            textureSize
        );
        context.drawTexture(
            RenderLayer::getGuiTextured,
            Screen.FOOTER_SEPARATOR_TEXTURE,
            x,
            y + height,
            0.0f,
            0.0f,
            width,
            lineHeight,
            textureSize,
            lineHeight
        );
    }
}
