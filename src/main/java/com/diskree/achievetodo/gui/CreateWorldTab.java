package com.diskree.achievetodo.gui;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.injection.WorldCreatorImpl;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CreateWorldTab extends GridScreenTab {

    private WorldScreenOptionGrid rewardsSection;
    private WorldScreenOptionGrid customGenerationSection;
    private WorldScreenOptionGrid lanSection;

    public CreateWorldTab(CreateWorldScreen screen) {
        super(Text.of(BuildConfig.MOD_NAME));
        if (screen == null || screen.client == null) {
            return;
        }
        WorldCreator worldCreator = screen.getWorldCreator();
        WorldCreatorImpl worldSettings = (WorldCreatorImpl) worldCreator;

        grid.getMainPositioner().alignHorizontalCenter();

        GridWidget.Adder rootContainer = grid.setColumnSpacing(10).setRowSpacing(8).createAdder(2);

        GridWidget.Adder rewardsTitleContainer = new GridWidget().setRowSpacing(4).createAdder(1);
        rewardsTitleContainer.add(new TextWidget(
            Text.translatable("achievetodo.world_creation_tab.rewards.title").copy()
                .formatted(Formatting.YELLOW), screen.client.textRenderer)
        );
        rootContainer.add(rewardsTitleContainer.getGridWidget(), 2, grid.copyPositioner().marginTop(24));

        WorldScreenOptionGrid.Builder rewardsSectionBuilder = WorldScreenOptionGrid.builder(170).marginLeft(1);
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
        rewardsSection = rewardsSectionBuilder.build();
        rootContainer.add(rewardsSection.getLayout(), 2);

        GridWidget.Adder customGenerationTitleContainer = new GridWidget().setRowSpacing(4).createAdder(1);
        customGenerationTitleContainer.add(new TextWidget(
            Text.translatable("achievetodo.world_creation_tab.generation.title").copy()
                .formatted(Formatting.YELLOW), screen.client.textRenderer)
        );
        rootContainer.add(customGenerationTitleContainer.getGridWidget(), 2);

        WorldScreenOptionGrid.Builder customGenerationSectionBuilder = WorldScreenOptionGrid.builder(170).marginLeft(1);
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
        customGenerationSection = customGenerationSectionBuilder.build();
        rootContainer.add(customGenerationSection.getLayout(), 2);

        GridWidget.Adder lanTitleContainer = new GridWidget().setRowSpacing(4).createAdder(1);
        lanTitleContainer.add(new TextWidget(
            Text.translatable("lanServer.title").copy()
                .formatted(Formatting.YELLOW), screen.client.textRenderer)
        );
        rootContainer.add(lanTitleContainer.getGridWidget(), 2);

        WorldScreenOptionGrid.Builder lanSectionBuilder = WorldScreenOptionGrid.builder(170).marginLeft(1);
        lanSectionBuilder.add(
            Text.translatable("achievetodo.world_creation_tab.lan.cooperative_mode"),
            worldSettings::achievetodo$isCooperativeModeEnabled,
            worldSettings::achievetodo$setCooperativeModeEnabled
        ).tooltip(Text.translatable("achievetodo.world_creation_tab.lan.cooperative_mode.tooltip"));
        lanSection = lanSectionBuilder.build();
        rootContainer.add(lanSection.getLayout(), 2);

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
    }
}
