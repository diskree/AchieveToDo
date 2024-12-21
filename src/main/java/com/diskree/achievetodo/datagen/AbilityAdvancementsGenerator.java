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
import net.minecraft.block.Block;
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

    public static final Block BACKGROUND = Blocks.PALE_MOSS_BLOCK;
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
            EAT_SUSPICIOUS_STEW,
        },
        {
            EAT_BEETROOT,
            EAT_CARROT,
            EAT_CHICKEN,
            EAT_DRIED_KELP,
            EAT_BEETROOT_SOUP,
            EAT_POTATO,
            EAT_APPLE,
            EAT_MELON_SLICE,
            EAT_COOKIE,
            EAT_MUSHROOM_STEW,
        },
        {
            OPEN_CHEST,
            OPEN_CRAFTING_TABLE,
            OPEN_STONECUTTER,
            OPEN_FURNACE,
            OPEN_ANVIL,
            OPEN_GRINDSTONE,
            OPEN_LOOM,
            OPEN_SMOKER,
        },
        {
            USE_GOLDEN_TOOLS,
            EQUIP_GOLDEN_ARMOR,
            USE_WOODEN_TOOLS,
            USE_STONE_TOOLS,
            EQUIP_LEATHER_ARMOR,
            USE_IRON_TOOLS,
            EQUIP_IRON_ARMOR,
            EQUIP_CHAINMAIL_ARMOR,
        },
        {
            JUMP,
            OPEN_DOOR,
            SLEEP,
            BREAK_BLOCKS,
            OPEN_INVENTORY,
            GET_INTO_BOAT,
            USE_SHIELD,
            USE_WATER_BUCKET,
            USE_SHEARS,
        },
        {
            TRADE_WITH_MASON,
            TRADE_WITH_CARTOGRAPHER,
            TRADE_WITH_LEATHERWORKER,
            TRADE_WITH_SHEPHERD,
            TRADE_WITH_BUTCHER,
            TRADE_WITH_FARMER,
            TRADE_WITH_CLERIC,
            TRADE_WITH_FISHERMAN,
            TRADE_WITH_FLETCHER,
            TRADE_WITH_ARMORER,
            TRADE_WITH_WEAPONSMITH,
            TRADE_WITH_TOOLSMITH,
            TRADE_WITH_LIBRARIAN,
        },
        {
            SHOOT_CROSSBOW,
            BREAK_BLOCKS_IN_NEGATIVE_Y,
            USE_FISHING_ROD,
            SHOOT_BOW,
            USE_BRUSH,
            USE_SPYGLASS,
            ATTACK_WITH_TRIDENT,
            THROW_ENDER_PEARL,
            GLIDE_WITH_FIREWORKS,
        },
        {
            ENTER_NETHER,
            EQUIP_DIAMOND_ARMOR,
            USE_DIAMOND_TOOLS,
            USE_NETHERITE_TOOLS,
            EQUIP_NETHERITE_ARMOR,
            ENTER_END,
            EQUIP_ELYTRA,
            TELEPORT_OUTER_ISLANDS,
        },
        {
            OPEN_BLAST_FURNACE,
            OPEN_CARTOGRAPHY_TABLE,
            OPEN_ENDER_CHEST,
            OPEN_BREWING_STAND,
            OPEN_SMITHING_TABLE,
            OPEN_BEACON,
            OPEN_SHULKER_BOX,
            OPEN_ENCHANTING_TABLE,
        },
        {
            EAT_RABBIT_STEW,
            EAT_HONEY_BOTTLE,
            EAT_PUMPKIN_PIE,
            EAT_GOLDEN_APPLE,
            EAT_ENCHANTED_GOLDEN_APPLE,
            EAT_RABBIT,
            EAT_MUTTON,
            EAT_PORKCHOP,
            EAT_BEEF,
            EAT_BAKED_POTATO,
        },
        {
            EAT_COOKED_SALMON,
            EAT_COOKED_COD,
            EAT_COOKED_RABBIT,
            EAT_COOKED_CHICKEN,
            EAT_COOKED_MUTTON,
            EAT_COOKED_PORKCHOP,
            EAT_COOKED_BEEF,
            EAT_BREAD,
            EAT_CHORUS_FRUIT,
            EAT_GOLDEN_CARROT,
        },
        {
            VISION,
            SWIM,
            SNEAK,
            SPRINT,
            UNLOCK_VAULT,
            USE_CAMPFIRE,
            USE_CAULDRON,
            USE_JUKEBOX,
            USE_COMPOSTER,
            PLACE_END_CRYSTAL,
            THROW_ENDER_EYE,
            CHARGE_RESPAWN_ANCHOR,
            OPEN_BARREL,
            IGNITE_TNT,
            OPEN_TRAPDOOR,
            OPEN_FENCE_GATE,
            GET_INTO_MINECART,
            USE_FLINT_AND_STEEL,
            PUT_IN_BUNDLE,
            THROW_EGG,
            THROW_SNOWBALL,
            THROW_WIND_CHARGE,
            DRINK_OMINOUS_BOTTLE,
            EQUIP_TURTLE_HELMET,
            ATTACK_WITH_MACE,
            TRADE_WITH_WANDERING_TRADER,
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
                    "textures/block/" + Registries.BLOCK.getId(BACKGROUND).getPath() + ".png"
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
