package com.diskree.achievetodo.blocked_actions.datagen;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
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
import net.minecraft.client.gui.screen.advancement.AdvancementTabType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.diskree.achievetodo.blocked_actions.BlockedActionType.*;

public class AdvancementsGenerator extends FabricAdvancementProvider {

    public static final String BLOCKED_ACTION_DEMYSTIFIED_CRITERION_PREFIX = "demystified_";
    public static final String BLOCKED_ACTIONS = "blocked_actions";

    public enum Tab {
        BIOMES(AdvancementTabType.LEFT),
        ADVENTURE(AdvancementTabType.LEFT),
        WEAPONRY(AdvancementTabType.LEFT),
        HUSBANDRY(AdvancementTabType.LEFT),
        MONSTERS(AdvancementTabType.LEFT),

        MINING(AdvancementTabType.ABOVE),
        BUILDING(AdvancementTabType.ABOVE),
        FARMING(AdvancementTabType.ABOVE),
        NETHER(AdvancementTabType.ABOVE),
        END(AdvancementTabType.ABOVE),

        BLOCKED_ACTIONS(AdvancementTabType.RIGHT),
        STATISTICS(AdvancementTabType.RIGHT),
        BACAP(AdvancementTabType.RIGHT),

        REDSTONE(AdvancementTabType.BELOW),
        POTION(AdvancementTabType.BELOW),
        ENCHANTING(AdvancementTabType.BELOW),
        CHALLENGES(AdvancementTabType.BELOW);

        private final AdvancementTabType position;

        Tab(AdvancementTabType position) {
            this.position = position;
        }

        public AdvancementTabType getPosition() {
            return position;
        }

        public int getOrder() {
            int order = 0;
            for (Tab tab : Tab.values()) {
                if (tab.position == position) {
                    if (tab == this) {
                        break;
                    }
                    order++;
                }
            }
            return order;
        }
    }

    public static final BlockedActionType[][] TREE = new BlockedActionType[][]{
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
            END_GATEWAY,
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

    protected AdvancementsGenerator(
        FabricDataOutput output,
        CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    public static Identifier buildAdvancementId(BlockedActionType blockedAction) {
        return buildAdvancementId(blockedAction.getName());
    }

    private static Identifier buildAdvancementId(String suffix) {
        return Identifier.of(BuildConfig.MOD_ID, BLOCKED_ACTIONS + "/" + suffix);
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
        for (BlockedActionType[] row : TREE) {
            for (BlockedActionType blockedAction : row) {
                Identifier id = buildAdvancementId(blockedAction);
                Item icon = blockedAction.getIcon();
                Text title = blockedAction.getTitle();
                Text description = blockedAction.getDescription();
                parentAdvancement = Advancement.Builder
                    .createUntelemetered()
                    .parent(parentAdvancement)
                    .display(icon, title, description, null, AdvancementFrame.TASK, true, false, false)
                    .rewards(AdvancementRewards.Builder.function(id))
                    .criterion(
                        BLOCKED_ACTION_DEMYSTIFIED_CRITERION_PREFIX + blockedAction.getName(),
                        Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())
                    )
                    .criterion("unblocked", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                    .build(id);
                consumer.accept(parentAdvancement);
            }
            parentAdvancement = rootAdvancement;
        }
    }
}
