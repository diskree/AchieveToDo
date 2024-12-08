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

    private static final boolean ALLOW_CUSTOM_GENERATION = true;

    private static final Text REWARDS_TITLE = Text.translatable("achievetodo.world_creation_tab.rewards.title");
    private static final Text ITEM_REWARDS = Text.translatable("achievetodo.world_creation_tab.rewards.item");
    private static final Text ITEM_REWARDS_INFO = Text.translatable("achievetodo.world_creation_tab.rewards.item.info");
    private static final Text EXPERIENCE_REWARDS = Text.translatable("achievetodo.world_creation_tab.rewards.experience");
    private static final Text EXPERIENCE_REWARDS_INFO = Text.translatable("achievetodo.world_creation_tab.rewards.experience.info");
    private static final Text TROPHY_REWARDS = Text.translatable("achievetodo.world_creation_tab.rewards.trophy");
    private static final Text TROPHY_REWARDS_INFO = Text.translatable("achievetodo.world_creation_tab.rewards.trophy.info");
    private static final Text CUSTOM_GENERATION_TITLE = Text.translatable("achievetodo.world_creation_tab.generation.title");
    private static final Text OVERWORLD_GENERATION = Text.translatable("achievetodo.world_creation_tab.generation.overworld");
    private static final Text OVERWORLD_GENERATION_INFO = Text.translatable("achievetodo.world_creation_tab.generation.overworld.info");
    private static final Text NETHER_GENERATION = Text.translatable("achievetodo.world_creation_tab.generation.nether");
    private static final Text NETHER_GENERATION_INFO = Text.translatable("achievetodo.world_creation_tab.generation.nether.info");
    private static final Text END_GENERATION = Text.translatable("achievetodo.world_creation_tab.generation.end");
    private static final Text END_GENERATION_INFO = Text.translatable("achievetodo.world_creation_tab.generation.end.info");
    private static final Text LAN_TITLE = Text.translatable("lanServer.title");
    private static final Text COOPERATIVE_MODE = Text.translatable("achievetodo.world_creation_tab.lan.cooperative_mode");
    private static final Text COOPERATIVE_MODE_INFO = Text.translatable("achievetodo.world_creation_tab.lan.cooperative_mode.info");

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
        rewardsTitleContainer.add(new TextWidget(REWARDS_TITLE.copy().formatted(Formatting.YELLOW), screen.client.textRenderer));
        rootContainer.add(rewardsTitleContainer.getGridWidget(), 2, grid.copyPositioner().marginTop(24));

        WorldScreenOptionGrid.Builder rewardsSectionBuilder = WorldScreenOptionGrid.builder(170).marginLeft(1);
        rewardsSectionBuilder.add(ITEM_REWARDS, worldSettings::achievetodo$isItemRewardsEnabled, worldSettings::achievetodo$setItemRewardsEnabled).tooltip(ITEM_REWARDS_INFO);
        rewardsSectionBuilder.add(EXPERIENCE_REWARDS, worldSettings::achievetodo$isExperienceRewardsEnabled, worldSettings::achievetodo$setExperienceRewardsEnabled).tooltip(EXPERIENCE_REWARDS_INFO);
        rewardsSectionBuilder.add(TROPHY_REWARDS, worldSettings::achievetodo$isTrophyRewardsEnabled, worldSettings::achievetodo$setTrophyRewardsEnabled).tooltip(TROPHY_REWARDS_INFO);
        rewardsSection = rewardsSectionBuilder.build();
        rootContainer.add(rewardsSection.getLayout(), 2);

        if (ALLOW_CUSTOM_GENERATION) {
            GridWidget.Adder customGenerationTitleContainer = new GridWidget().setRowSpacing(4).createAdder(1);
            customGenerationTitleContainer.add(new TextWidget(CUSTOM_GENERATION_TITLE.copy().formatted(Formatting.YELLOW), screen.client.textRenderer));
            rootContainer.add(customGenerationTitleContainer.getGridWidget(), 2);

            WorldScreenOptionGrid.Builder customGenerationSectionBuilder = WorldScreenOptionGrid.builder(170).marginLeft(1);
            customGenerationSectionBuilder.add(OVERWORLD_GENERATION, worldSettings::achievetodo$isTerralithEnabled, worldSettings::achievetodo$setTerralithEnabled).tooltip(OVERWORLD_GENERATION_INFO);
            customGenerationSectionBuilder.add(NETHER_GENERATION, worldSettings::achievetodo$isAmplifiedNetherEnabled, worldSettings::achievetodo$setAmplifiedNetherEnabled).tooltip(NETHER_GENERATION_INFO);
            customGenerationSectionBuilder.add(END_GENERATION, worldSettings::achievetodo$isNullscapeEnabled, worldSettings::achievetodo$setNullscapeEnabled).tooltip(END_GENERATION_INFO);
            customGenerationSection = customGenerationSectionBuilder.build();
            rootContainer.add(customGenerationSection.getLayout(), 2);
        }

        GridWidget.Adder lanTitleContainer = new GridWidget().setRowSpacing(4).createAdder(1);
        lanTitleContainer.add(new TextWidget(LAN_TITLE.copy().formatted(Formatting.YELLOW), screen.client.textRenderer));
        rootContainer.add(lanTitleContainer.getGridWidget(), 2);

        WorldScreenOptionGrid.Builder lanSectionBuilder = WorldScreenOptionGrid.builder(170).marginLeft(1);
        lanSectionBuilder.add(COOPERATIVE_MODE, worldSettings::achievetodo$isCooperativeModeEnabled, worldSettings::achievetodo$setCooperativeModeEnabled).tooltip(COOPERATIVE_MODE_INFO);
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
