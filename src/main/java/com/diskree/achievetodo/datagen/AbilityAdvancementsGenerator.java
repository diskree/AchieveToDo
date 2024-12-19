package com.diskree.achievetodo.datagen;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.BuildConfig;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.diskree.achievetodo.AbilityType.*;

public class AbilityAdvancementsGenerator extends FabricAdvancementProvider {

    public static final String DEMYSTIFIED_CRITERION_PREFIX = BuildConfig.MOD_ID + "_" + "demystified" + "_";
    public static final String UNLOCKED_CRITERION = BuildConfig.MOD_ID + "_" + "unlocked";

    public static final String ABILITY_PATH_PREFIX = "abilities/";
    public static final AbilityType[][] TREE = new AbilityType[][]{
        {
            EAT_SALMON,
            EAT_COD,
            EAT_TROPICAL_FISH,
            EAT_ROTTEN_FLESH,
            EAT_SPIDER_EYE,
            EAT_SWEET_BERRIES,
            EAT_GLOW_BERRIES,
            EAT_PUFFERFISH,
            EAT_POISONOUS_POTATO,
            EAT_CHORUS_FRUIT,
        },
        {
            EAT_SUSPICIOUS_STEW,
            EAT_BEETROOT,
            EAT_CARROT,
            EAT_CHICKEN,
            EAT_DRIED_KELP,
            EAT_BEETROOT_SOUP,
            EAT_POTATO,
            EAT_APPLE,
            EAT_MELON_SLICE,
            EAT_COOKIE,
        },
        {
            OPEN_CHEST,
            USING_CRAFTING_TABLE,
            USING_STONECUTTER,
            OPEN_FURNACE,
            USING_ANVIL,
            USING_GRINDSTONE,
            USING_LOOM,
            OPEN_SMOKER,
        },
        {
            USING_GOLDEN_TOOLS,
            EQUIP_GOLDEN_ARMOR,
            USING_WOODEN_TOOLS,
            USING_STONE_TOOLS,
            EQUIP_LEATHER_ARMOR,
            USING_IRON_TOOLS,
            EQUIP_IRON_ARMOR,
            EQUIP_CHAINMAIL_ARMOR,
        },
        {
            JUMP,
            OPEN_DOOR,
            SLEEP,
            OPEN_INVENTORY,
            BREAK_BLOCKS,
            USING_BOAT,
            USING_SHIELD,
            USING_WATER_BUCKET,
            USING_SHEARS,
        },
        {
            VILLAGER_MASON,
            VILLAGER_CARTOGRAPHER,
            VILLAGER_LEATHERWORKER,
            VILLAGER_SHEPHERD,
            VILLAGER_BUTCHER,
            VILLAGER_FARMER,
            VILLAGER_CLERIC,
            VILLAGER_FISHERMAN,
            VILLAGER_FLETCHER,
            VILLAGER_ARMORER,
            VILLAGER_WEAPONSMITH,
            VILLAGER_TOOLSMITH,
            VILLAGER_LIBRARIAN,
        },
        {
            USING_CROSSBOW,
            BREAK_BLOCKS_IN_NEGATIVE_Y,
            USING_FISHING_ROD,
            USING_BOW,
            USING_BRUSH,
            USING_SPYGLASS,
            THROW_TRIDENT,
            THROW_ENDER_PEARL,
            FLY,
        },
        {
            NETHER,
            EQUIP_DIAMOND_ARMOR,
            USING_DIAMOND_TOOLS,
            USING_NETHERITE_TOOLS,
            EQUIP_NETHERITE_ARMOR,
            END,
            EQUIP_ELYTRA,
            OUTER_ISLANDS,
        },
        {
            OPEN_BLAST_FURNACE,
            USING_CARTOGRAPHY_TABLE,
            OPEN_ENDER_CHEST,
            OPEN_BREWING_STAND,
            USING_SMITHING_TABLE,
            USING_BEACON,
            OPEN_SHULKER_BOX,
            USING_ENCHANTING_TABLE,
        },
        {
            EAT_MUSHROOM_STEW,
            EAT_RABBIT_STEW,
            EAT_HONEY_BOTTLE,
            EAT_PUMPKIN_PIE,
            EAT_GOLDEN_APPLE,
            EAT_ENCHANTED_GOLDEN_APPLE,
            EAT_RABBIT,
            EAT_MUTTON,
            EAT_PORKCHOP,
            EAT_BEEF,
        },
        {
            EAT_BAKED_POTATO,
            EAT_COOKED_SALMON,
            EAT_COOKED_COD,
            EAT_COOKED_RABBIT,
            EAT_COOKED_CHICKEN,
            EAT_COOKED_MUTTON,
            EAT_COOKED_PORKCHOP,
            EAT_COOKED_BEEF,
            EAT_BREAD,
            EAT_GOLDEN_CARROT,
        },
    };

    protected AbilityAdvancementsGenerator(
        FabricDataOutput output,
        CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    public static Identifier buildAdvancementId(AbilityType ability) {
        return buildAdvancementId(ability.getLowerCaseName());
    }

    private static Identifier buildAdvancementId(String suffix) {
        return Identifier.of(BuildConfig.MOD_ID, ABILITY_PATH_PREFIX + suffix);
    }

    @Override
    public void generateAdvancement(
        RegistryWrapper.WrapperLookup registryLookup,
        Consumer<AdvancementEntry> consumer
    ) {
        AdvancementEntry rootAdvancement = Advancement.Builder
            .createUntelemetered()
            .display(
                Items.BARRIER,
                Text.of(BuildConfig.MOD_NAME),
                Text.translatable(BuildConfig.MOD_ID + ".root.description"),
                Identifier.ofVanilla(
                    "textures/block/" + Registries.BLOCK.getId(Blocks.PALE_OAK_PLANKS).getPath() + ".png"
                ),
                AdvancementFrame.TASK,
                false,
                false,
                false
            )
            .criterion("tick", TickCriterion.Conditions.createTick())
            .build(buildAdvancementId("root"));
        consumer.accept(rootAdvancement);

        AdvancementEntry parentAdvancement = rootAdvancement;
        for (AbilityType[] row : TREE) {
            for (AbilityType ability : row) {
                Identifier id = buildAdvancementId(ability);
                Item icon = ability.getIcon();
                if (icon == null) {
                    throw new IllegalStateException("Ability " + ability + " haven't icon!");
                }
                Text title = ability.getTitle();
                Text description = ability.getDescription();
                parentAdvancement = Advancement.Builder
                    .createUntelemetered()
                    .parent(parentAdvancement)
                    .display(icon, title, description, null, AdvancementFrame.TASK, true, false, false)
                    .rewards(AdvancementRewards.Builder.function(id))
                    .criterion(
                        DEMYSTIFIED_CRITERION_PREFIX + ability.getLowerCaseName(),
                        Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())
                    )
                    .criterion(
                        UNLOCKED_CRITERION,
                        Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())
                    )
                    .build(id);
                consumer.accept(parentAdvancement);
            }
            parentAdvancement = rootAdvancement;
        }
    }
}
