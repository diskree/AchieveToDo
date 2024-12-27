package com.diskree.achievetodo;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DynamicProgressType {

    AN_APPLE_A_DAY(
        "blazeandcave:farming/an_apple_a_day",
        30,
        false,
        "bac_apple_days"
    ),
    JUST_KEEPS_GOING(
        "blazeandcave:animal/just_keeps_going",
        3600,
        true,
        "bac_just_keep"
    ),
    CAPTAIN_AMERICA(
        "blazeandcave:biomes/captain_america",
        1200,
        true,
        "bac_captain_america"
    ),
    LET_ME_OUT(
        "blazeandcave:nether/let_me_out",
        24000,
        true,
        "bac_let_me_out"
    ),
    I_YEARNED_FOR_THE_MINES(
        "blazeandcave:mining/i_yearned_for_the_mines",
        6000,
        true,
        "bac_i_yearned_for_the_mines"
    ),
    CASTAWAY(
        "blazeandcave:farming/castaway",
        3600,
        true,
        "bac_castaway"
    ),
    FREE_DIVER(
        "blazeandcave:biomes/free_diver",
        120,
        true,
        "bac_underwater"
    ),
    SLEEP_WITH_THE_FISHES(
        "blazeandcave:biomes/sleep_with_the_fishes",
        1200,
        true,
        "bac_underwater"
    ),
    PASSING_THE_TIME(
        "blazeandcave:statistics/passing_the_time",
        100,
        false,
        "bac_day_count"
    ),
    HAPPY_NEW_YEAR(
        "blazeandcave:statistics/happy_new_year",
        365,
        false,
        "bac_day_count"
    ),
    HALF_HEART_LIFE(
        "blazeandcave:weaponry/half_heart_life",
        60,
        false,
        "bac_hh_life"
    ),
    HOUSE_OF_FREAKS(
        "blazeandcave:monsters/house_of_freaks",
        5,
        false,
        "bac_warden_count"
    ),
    OVERWARDEN(
        "blazeandcave:challenges/overwarden",
        50,
        false,
        "bac_overwarden_count"
    ),
    DIVERS_DOZEN(
        "blazeandcave:end/divers_dozen",
        12,
        false,
        "bac_divers_dozen_count"
    ),
    FACTORIO(
        "blazeandcave:redstone/factorio",
        64,
        false,
        "bac_factorio_count"
    ),
    A_PIGLINS_BEST_FRIEND(
        "blazeandcave:nether/a_piglins_best_friend",
        500,
        false,
        "bac_pigling"
    ),
    THE_WORLD_IS_ENDING(
        "blazeandcave:challenges/the_world_is_ending",
        10,
        false,
        "bac_ten_withers"
    ),
    VAULT_HUNTER(
        "blazeandcave:adventure/vault_hunter",
        25,
        false,
        "bac_vault_hunter_count"
    ),
    WHACK_A_MOLE(
        "blazeandcave:enchanting/whack_a_mole",
        8,
        false,
        "bac_whack_a_mole_count"
    ),
    OM_NOM_NOM(
        "blazeandcave:statistics/om_nom_nom",
        200,
        false,
        "bac_stat_food"
    ),
    YUM_YUM_YUMMO(
        "blazeandcave:statistics/yum_yum_yummo",
        1000,
        false,
        "bac_stat_food"
    ),
    FOOD_GLORIOUS_FOOD(
        "blazeandcave:statistics/food_glorious_food",
        5000,
        false,
        "bac_stat_food"
    ),
    LOOT_EM(
        "blazeandcave:statistics/loot_em",
        10,
        false,
        "bac_stat_loot_chest"
    ),
    MORE_FOR_ME(
        "blazeandcave:statistics/more_for_me",
        100,
        false,
        "bac_stat_loot_chest"
    ),
    I_HEART_CHESTS(
        "blazeandcave:statistics/i_heart_chests",
        500,
        false,
        "bac_stat_loot_chest"
    ),
    ON_A_RAIL(
        "blazeandcave:redstone/on_a_rail",
        1000,
        true,
        "bac_oar_eligible_x", "bac_oar_eligible_z",
        "bac_oar_current_x", "bac_oar_current_z"
    ),
    ARTILLERY(
        "blazeandcave:weaponry/artillery",
        9,
        false,
        "bac_inv_artillery"
    ),
    CHESTFUL_OF_COBBLESTONE(
        "blazeandcave:mining/chestful_of_cobblestone",
        1728,
        true,
        "bac_inv_chestful_of_cobblestone"
    ),
    IMMORTAL(
        "blazeandcave:challenges/immortal",
        37,
        false,
        "bac_inv_immortal"
    ),
    ROCKETMAN(
        "blazeandcave:end/rocketman",
        2368,
        true,
        "bac_inv_rocketman"
    ),
    LOSER(
        "blazeandcave:weaponry/loser",
        2,
        false,
        "bac_loser"
    );

    private final String advancementId;
    private final List<String> objectiveNames;
    private final int finalValue;
    private final boolean isPercentage;

    DynamicProgressType(String advancementId, int finalValue, boolean isPercentage, String... trackedObjectiveNames) {
        this.advancementId = advancementId;
        this.finalValue = finalValue;
        this.isPercentage = isPercentage;
        this.objectiveNames = Arrays.stream(trackedObjectiveNames).toList();
    }

    public int getFinalValue() {
        return finalValue;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    @Nullable
    public static DynamicProgressType findByAdvancementId(@NotNull Identifier advancementId) {
        for (DynamicProgressType progress : DynamicProgressType.values()) {
            if (advancementId.toString().equals(progress.advancementId)) {
                return progress;
            }
        }
        return null;
    }

    public static @Nullable List<DynamicProgressType> findByObjectiveName(String objectiveName) {
        List<DynamicProgressType> progressTypes = null;
        for (DynamicProgressType progress : values()) {
            if (progress.objectiveNames.contains(objectiveName)) {
                if (progressTypes == null) {
                    progressTypes = new ArrayList<>();
                }
                progressTypes.add(progress);
            }
        }
        return progressTypes;
    }
}
