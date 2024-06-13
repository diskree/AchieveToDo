package com.diskree.achievetodo.gui;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.api.AchieveToDoAddon;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import com.diskree.achievetodo.config.Configuration;
import com.diskree.achievetodo.injection.WorldCreatorImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateWorldTab extends GridScreenTab {

    private final List<Configuration> configurations = new ArrayList<>();
    private final List<AchieveToDoAddon> addons = new ArrayList<>();
    private final CyclingButtonWidget<Configuration> selectPresetButton;

    public CreateWorldTab(@NotNull CreateWorldScreen screen) {
        super(Text.of(BuildConfig.MOD_NAME));
        Configuration vanillaConfiguration = initVanillaConfiguration();
        configurations.add(vanillaConfiguration);
        FabricLoader.getInstance()
            .getEntrypointContainers(BuildConfig.MOD_ID, AchieveToDoAddon.class)
            .forEach(entrypoint -> addons.add(entrypoint.getEntrypoint()));
        for (AchieveToDoAddon addon : addons) {
            configurations.add(new Configuration(addon.getName(), addon.getPreset()));
        }

        WorldCreatorImpl worldCreator = (WorldCreatorImpl) screen.getWorldCreator();
        worldCreator.achievetodo$setConfiguration(vanillaConfiguration);

        GridWidget.Adder adder = grid.setColumnSpacing(10).setRowSpacing(8).createAdder(2);

        selectPresetButton = adder.add(
            CyclingButtonWidget.builder(Configuration::getPresetNameText).values(configurations).build(
                0,
                0,
                150,
                20,
                Text.translatable("createWorld.customize.presets.select"),
                (button, configuration) -> {
                    for (AchieveToDoAddon addon : addons) {
                        addon.onPresetEnabled(addon.getName().equals(configuration.presetName()));
                    }
                    worldCreator.achievetodo$setConfiguration(configuration);
                }
            )
        );
        selectPresetButton.setValue(worldCreator.achievetodo$getConfiguration());
        adder.add(ButtonWidget.builder(
            Text.translatable("selectWorld.customizeType"),
            button -> openCustomizeScreen()
        ).build());
    }

    public void enablePreset(String presetName) {
        for (Configuration configuration : configurations) {
            if (presetName.equals(configuration.presetName())) {
                selectPresetButton.setValue(configuration);
                return;
            }
        }
    }

    private void openCustomizeScreen() {

    }

    private @NotNull Configuration initVanillaConfiguration() {
        HashMap<BlockedActionType, Integer> counts = new HashMap<>();
        for (BlockedActionType blockedAction : BlockedActionType.values()) {
            int count = switch (blockedAction) {
                case JUMP -> 1;
                case OPEN_DOOR -> 2;
                case SLEEP -> 3;
                case OPEN_INVENTORY -> 4;
                case BREAK_BLOCKS_IN_POSITIVE_Y -> 5;
                case USING_BOAT -> 6;
                case USING_SHIELD -> 7;
                case USING_WATER_BUCKET -> 8;
                case USING_SHEARS -> 9;
                case USING_CROSSBOW -> 10;
                case BREAK_BLOCKS_IN_NEGATIVE_Y -> 11;
                case USING_FISHING_ROD -> 12;
                case USING_BOW -> 13;
                case USING_BRUSH -> 14;
                case USING_SPYGLASS -> 15;
                case THROW_TRIDENT -> 16;
                case THROW_ENDER_PEARL -> 17;
                case EQUIP_ELYTRA -> 18;
                case END_GATEWAY -> 19;
                case FLY -> 20;
                case OPEN_SHULKER_BOX -> 21;
                case EAT_SALMON -> 22;
                case EAT_COD -> 23;
                case EAT_TROPICAL_FISH -> 24;
                case EAT_ROTTEN_FLESH -> 25;
                case EAT_SPIDER_EYE -> 26;
                case EAT_SWEET_BERRIES -> 27;
                case EAT_GLOW_BERRIES -> 28;
                case EAT_PUFFERFISH -> 29;
                case EAT_POISONOUS_POTATO -> 30;
                case EAT_CHORUS_FRUIT -> 31;
                case EAT_SUSPICIOUS_STEW -> 32;
                case EAT_BEETROOT -> 33;
                case EAT_CARROT -> 34;
                case EAT_CHICKEN -> 35;
                case EAT_DRIED_KELP -> 36;
                case EAT_BEETROOT_SOUP -> 37;
                case EAT_POTATO -> 38;
                case EAT_APPLE -> 39;
                case EAT_MELON_SLICE -> 40;
                case EAT_COOKIE -> 41;
                case EAT_MUSHROOM_STEW -> 42;
                case EAT_RABBIT_STEW -> 43;
                case EAT_HONEY_BOTTLE -> 44;
                case EAT_PUMPKIN_PIE -> 45;
                case EAT_GOLDEN_APPLE -> 46;
                case EAT_ENCHANTED_GOLDEN_APPLE -> 47;
                case EAT_RABBIT -> 48;
                case EAT_MUTTON -> 49;
                case EAT_PORKCHOP -> 50;
                case EAT_BEEF -> 51;
                case EAT_BAKED_POTATO -> 52;
                case EAT_COOKED_SALMON -> 53;
                case EAT_COOKED_COD -> 54;
                case EAT_COOKED_RABBIT -> 55;
                case EAT_COOKED_CHICKEN -> 56;
                case EAT_COOKED_MUTTON -> 57;
                case EAT_COOKED_PORKCHOP -> 58;
                case EAT_COOKED_BEEF -> 59;
                case EAT_BREAD -> 60;
                case EAT_GOLDEN_CARROT -> 61;
                case OPEN_CHEST -> 62;
                case USING_CRAFTING_TABLE -> 63;
                case USING_STONECUTTER -> 64;
                case OPEN_FURNACE -> 65;
                case USING_ANVIL -> 66;
                case USING_GRINDSTONE -> 67;
                case USING_LOOM -> 68;
                case OPEN_SMOKER -> 69;
                case OPEN_BLAST_FURNACE -> 70;
                case USING_CARTOGRAPHY_TABLE -> 71;
                case OPEN_ENDER_CHEST -> 72;
                case OPEN_BREWING_STAND -> 73;
                case USING_SMITHING_TABLE -> 74;
                case USING_BEACON -> 75;
                case USING_ENCHANTING_TABLE -> 76;
                case USING_GOLDEN_TOOLS -> 77;
                case USING_WOODEN_TOOLS -> 78;
                case USING_STONE_TOOLS -> 79;
                case USING_IRON_TOOLS -> 80;
                case USING_DIAMOND_TOOLS -> 81;
                case USING_NETHERITE_TOOLS -> 82;
                case EQUIP_GOLDEN_ARMOR -> 83;
                case EQUIP_LEATHER_ARMOR -> 84;
                case EQUIP_IRON_ARMOR -> 85;
                case EQUIP_CHAINMAIL_ARMOR -> 86;
                case EQUIP_DIAMOND_ARMOR -> 87;
                case EQUIP_NETHERITE_ARMOR -> 88;
                case NETHER -> 89;
                case END -> 90;
                case VILLAGER_MASON -> 91;
                case VILLAGER_CARTOGRAPHER -> 92;
                case VILLAGER_LEATHERWORKER -> 93;
                case VILLAGER_SHEPHERD -> 94;
                case VILLAGER_BUTCHER -> 95;
                case VILLAGER_FARMER -> 96;
                case VILLAGER_CLERIC -> 97;
                case VILLAGER_FISHERMAN -> 98;
                case VILLAGER_FLETCHER -> 99;
                case VILLAGER_ARMORER -> 100;
                case VILLAGER_WEAPONSMITH -> 101;
                case VILLAGER_TOOLSMITH -> 102;
                case VILLAGER_LIBRARIAN -> 103;
            };
            counts.put(blockedAction, count);
        }
        return new Configuration("Minecraft", counts);
    }
}
