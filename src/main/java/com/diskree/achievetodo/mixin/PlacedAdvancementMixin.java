package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.datagen.AbilityAdvancementsGenerator;
import com.diskree.achievetodo.gui.AdvancementsTabType;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.logging.Logger;

@Mixin(PlacedAdvancement.class)
public class PlacedAdvancementMixin {

    @Unique
    private static final Map<String, List<String>> customChildrenOrderMap = new HashMap<>();

    static {
        customChildrenOrderMap.put("blazeandcave:statistics/root", List.of(
            "blazeandcave:statistics/out_for_a_stroll",
            "blazeandcave:statistics/natural_sprinter",
            "blazeandcave:statistics/sneaky_snitch",
            "blazeandcave:statistics/laps_in_the_pool",
            "blazeandcave:statistics/luxury_cruise",
            "blazeandcave:technical/big_cheater",
            "blazeandcave:statistics/minecart_rider",
            "blazeandcave:statistics/pig_training",
            "blazeandcave:statistics/taking_it_in_stride",
            "blazeandcave:statistics/horse_training",
            "blazeandcave:statistics/take_to_the_skies"
        ));
        customChildrenOrderMap.put("blazeandcave:technical/you_are_a_big_cheater", List.of(
            "blazeandcave:statistics/the_first_night",
            "blazeandcave:statistics/spring_in_your_step",
            "blazeandcave:statistics/om_nom_nom",
            "blazeandcave:statistics/mob_hunter",
            "blazeandcave:statistics/level_up",
            "blazeandcave:statistics/loot_em",
            "blazeandcave:statistics/the_parrots_and_the_bats",
            "blazeandcave:statistics/novice_enchanter",
            "blazeandcave:statistics/the_haggler"
        ));
        customChildrenOrderMap.put("blazeandcave:challenges/root", List.of(
            "blazeandcave:challenges/nuclear_fusion",
            "blazeandcave:challenges/ad_astra",
            "blazeandcave:challenges/all_the_blocks",
            "blazeandcave:challenges/constellation",
            "blazeandcave:challenges/ultimate_enchanter",
            "blazeandcave:challenges/i_am_loot",
            "blazeandcave:challenges/telescopic",
            "blazeandcave:challenges/were_in_the_endgame_now"
        ));
        customChildrenOrderMap.put("blazeandcave:potion/root", List.of(
            "blazeandcave:potion/failed_concoctions",
            "blazeandcave:potion/performance_enhancing_drugs",
            "blazeandcave:potion/medic",
            "blazeandcave:potion/secret_of_the_ooze",
            "blazeandcave:potion/death_by_magic",
            "minecraft:end/dragon_breath",
            "blazeandcave:potion/stayin_frosty"
        ));
        customChildrenOrderMap.put("blazeandcave:potion/the_invisible_player", List.of(
            "blazeandcave:potion/true_feather_falling",
            "blazeandcave:potion/stealth_takedown"
        ));
        customChildrenOrderMap.put("minecraft:nether/all_potions", List.of(
            "blazeandcave:potion/a_much_more_doable_challenge",
            "blazeandcave:potion/a_furious_test_subject"
        ));
        customChildrenOrderMap.put("blazeandcave:enchanting/root", List.of(
            "blazeandcave:enchanting/librarian",
            "blazeandcave:enchanting/like_a_cat",
            "blazeandcave:enchanting/heavy_metal"
        ));
        customChildrenOrderMap.put("blazeandcave:enchanting/librarian", List.of(
            "blazeandcave:enchanting/super_efficient",
            "blazeandcave:enchanting/needle_sharp",
            "blazeandcave:enchanting/machine_bow",
            "blazeandcave:enchanting/bow_down_to_me",
            "blazeandcave:enchanting/boomerang",
            "blazeandcave:enchanting/mace_windu",
            "blazeandcave:enchanting/magical_stockpile"
        ));
        customChildrenOrderMap.put("blazeandcave:enchanting/needle_sharp", List.of(
            "blazeandcave:enchanting/undead_slayer",
            "blazeandcave:enchanting/overkill"
        ));
        customChildrenOrderMap.put("blazeandcave:enchanting/fiery", List.of(
            "blazeandcave:enchanting/like_a_ninja",
            "blazeandcave:enchanting/gotta_go_fast"
        ));
        customChildrenOrderMap.put("blazeandcave:enchanting/heavy_metal", List.of(
            "blazeandcave:enchanting/this_name_sounds_cooler",
            "blazeandcave:enchanting/curses",
            "blazeandcave:enchanting/unbreakable",
            "blazeandcave:enchanting/baron_of_blacksmiths"
        ));
        customChildrenOrderMap.put("blazeandcave:enchanting/unbreakable", List.of(
            "blazeandcave:enchanting/master_miner",
            "blazeandcave:enchanting/master_knight",
            "blazeandcave:enchanting/master_fisher"
        ));
        customChildrenOrderMap.put("blazeandcave:redstone/root", List.of(
            "blazeandcave:redstone/click",
            "blazeandcave:redstone/all_aboard",
            "blazeandcave:redstone/powerful_light",
            "blazeandcave:redstone/mozart",
            "blazeandcave:redstone/vibe_check"
        ));
        customChildrenOrderMap.put("blazeandcave:redstone/fuel_engine", List.of(
            "blazeandcave:redstone/moving_storage",
            "blazeandcave:redstone/electric_rails"
        ));
        customChildrenOrderMap.put("blazeandcave:redstone/under_pressure", List.of(
            "blazeandcave:redstone/tripping_over",
            "blazeandcave:redstone/pressure_detector"
        ));
        customChildrenOrderMap.put("blazeandcave:redstone/repeating_repeater", List.of(
            "blazeandcave:redstone/quirky_quartz",
            "blazeandcave:redstone/space_hopper",
            "blazeandcave:redstone/moving_parts"
        ));
        customChildrenOrderMap.put("blazeandcave:redstone/the_block_of_eternal_screaming", List.of(
            "blazeandcave:redstone/the_incredible_sculk",
            "blazeandcave:redstone/cataclyst"
        ));
        customChildrenOrderMap.put("blazeandcave:biomes/root", List.of(
            "blazeandcave:biomes/just_keep_swimming",
            "blazeandcave:biomes/smooth_operator",
            "blazeandcave:biomes/for_you_my_sweet",
            "blazeandcave:biomes/kilometre_walk"
        ));
        customChildrenOrderMap.put("blazeandcave:biomes/kilometre_walk", List.of(
            "blazeandcave:biomes/ten_thousand_blocks",
            "blazeandcave:biomes/one_small_steppe_for_man",
            "blazeandcave:biomes/one_with_the_forest",
            "blazeandcave:biomes/the_boreal_deal"
        ));
        customChildrenOrderMap.put("blazeandcave:biomes/this_snow_is_snowier", List.of(
            "blazeandcave:biomes/powder_full",
            "minecraft:adventure/walk_on_powder_snow_with_leather_boots"
        ));
        customChildrenOrderMap.put("minecraft:adventure/root", List.of(
            "blazeandcave:adventure/out_of_posts",
            "blazeandcave:adventure/now_youre_thinking_with_portals",
            "blazeandcave:adventure/do_you_want_to_trade",
            "blazeandcave:adventure/travelling_merchant",
            "blazeandcave:adventure/i_hereby_dub_thee"
        ));
        customChildrenOrderMap.put("minecraft:adventure/trade", List.of(
            "blazeandcave:adventure/crazy_cat_lady",
            "blazeandcave:adventure/knowledge_is_power",
            "blazeandcave:adventure/hey_you_two_should_kiss",
            "blazeandcave:adventure/filthy_rich",
            "blazeandcave:adventure/traveller",
            "minecraft:adventure/summon_iron_golem"
        ));
        customChildrenOrderMap.put("blazeandcave:adventure/knowledge_is_power", List.of(
            "blazeandcave:adventure/rare_candy_shop",
            "blazeandcave:adventure/all_chained_up",
            "blazeandcave:adventure/mapmaker"
        ));
        customChildrenOrderMap.put("blazeandcave:adventure/treasure_map", List.of(
            "minecraft:adventure/minecraft_trials_edition",
            "blazeandcave:adventure/monumental",
            "blazeandcave:adventure/house_of_psychos"
        ));
        customChildrenOrderMap.put("blazeandcave:adventure/mapmaker", List.of(
            "blazeandcave:adventure/treasure_map",
            "blazeandcave:adventure/mapmakers_table"
        ));
        customChildrenOrderMap.put("blazeandcave:adventure/travelling_merchant", List.of(
            "blazeandcave:adventure/shady_deals",
            "blazeandcave:adventure/florist"
        ));
        customChildrenOrderMap.put("blazeandcave:adventure/traveller", List.of(
            "blazeandcave:adventure/village_settler",
            "blazeandcave:adventure/professional"
        ));

    }

    @Unique
    private List<PlacedAdvancement> sortedChildren = null;

    @Shadow
    @Final
    private Set<PlacedAdvancement> children;

    @Shadow
    @Final
    private AdvancementEntry advancementEntry;

    @Inject(
        method = "getChildren",
        at = @At("HEAD"),
        cancellable = true
    )
    public void setCustomChildrenOrder(CallbackInfoReturnable<Iterable<PlacedAdvancement>> cir) {
        if (sortedChildren != null) {
            cir.setReturnValue(sortedChildren);
            return;
        }
        if (children.size() <= 1) {
            return;
        }

        Identifier advancementId = advancementEntry.id();
        AdvancementsTabType tab = AdvancementsTabType.findByAdvancement(advancementId);
        List<PlacedAdvancement> sortedChildren = new ArrayList<>(children);

        if (tab == AdvancementsTabType.ABILITIES && advancementEntry.value().isRoot()) {
            List<Identifier> rowsOrder = new ArrayList<>();
            for (AbilityType[] row : AbilityAdvancementsGenerator.TREE) {
                Arrays.sort(row, Comparator.comparingInt(AbilityType::getRequiredAdvancementsCount));
                rowsOrder.add(AbilityAdvancementsGenerator.buildAdvancementId(row[0]));
            }
            sortedChildren.sort((placedAdvancement, otherPlacedAdvancement) -> {
                Integer index = rowsOrder.indexOf(placedAdvancement.getAdvancementEntry().id());
                Integer otherIndex = rowsOrder.indexOf(otherPlacedAdvancement.getAdvancementEntry().id());
                return index.compareTo(otherIndex);
            });
        } else if (customChildrenOrderMap.containsKey(advancementId.toString())) {
            List<String> childOrder = customChildrenOrderMap.get(advancementId.toString());
            List<String> actualChildIds = children.stream()
                .map(child -> child.getAdvancementEntry().id().toString())
                .toList();
            for (String childId : actualChildIds) {
                if (!childOrder.contains(childId)) {
                    Logger.getLogger(BuildConfig.MOD_NAME).warning(
                        "Child ID '" + childId + "' is missing in customChildOrderMap for parent ID '" + advancementId +
                            "'. Existing children: " + actualChildIds
                    );
                    return;
                }
            }
            for (String childId : childOrder) {
                if (!actualChildIds.contains(childId)) {
                    Logger.getLogger(BuildConfig.MOD_NAME).warning(
                        "CustomChildOrderMap contains invalid child ID '" + childId +
                            "' for parent ID '" + advancementId + "'. Existing children: " + actualChildIds
                    );
                    return;
                }
            }
            sortedChildren.sort(Comparator.comparingInt(child ->
                childOrder.indexOf(child.getAdvancementEntry().id().toString())
            ));
        } else {
            sortedChildren.sort(Comparator.comparing(placedAdvancement ->
                placedAdvancement.getAdvancementEntry().id()
            ));
        }
        this.sortedChildren = sortedChildren;
        cir.setReturnValue(sortedChildren);
    }
}
