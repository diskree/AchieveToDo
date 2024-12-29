package com.diskree.achievetodo.datagen;

import com.diskree.achievetodo.AbilitiesBranchType;
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
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AbilityAdvancementsGenerator extends FabricAdvancementProvider {

    public static final String DEMYSTIFIED_CRITERION_PREFIX = BuildConfig.MOD_ID + "_" + "demystified" + "_";
    public static final String UNLOCKED_CRITERION = BuildConfig.MOD_ID + "_" + "unlocked";

    public static final String ABILITY_PATH_PREFIX = "abilities/";

    public static final Block TAB_BACKGROUND = Blocks.PALE_MOSS_BLOCK;

    protected AbilityAdvancementsGenerator(
        FabricDataOutput output,
        CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    public static @NotNull Identifier buildAdvancementId(@NotNull AbilityType ability) {
        return buildAdvancementId(ability.getLowerCaseName());
    }

    private static @NotNull Identifier buildAdvancementId(String suffix) {
        return Identifier.of(BuildConfig.MOD_ID, ABILITY_PATH_PREFIX + suffix);
    }

    @Override
    public void generateAdvancement(
        RegistryWrapper.WrapperLookup registryLookup,
        @NotNull Consumer<AdvancementEntry> consumer
    ) {
        AdvancementEntry rootAdvancement = Advancement.Builder
            .createUntelemetered()
            .display(
                Items.BARRIER,
                Text.of(BuildConfig.MOD_NAME),
                Text.translatable(BuildConfig.MOD_ID + ".description"),
                Identifier.ofVanilla("textures/block/" + Registries.BLOCK.getId(TAB_BACKGROUND).getPath() + ".png"),
                AdvancementFrame.TASK,
                false,
                false,
                false
            )
            .criterion("tick", TickCriterion.Conditions.createTick())
            .build(buildAdvancementId("root"));
        consumer.accept(rootAdvancement);

        for (AbilityType ability : AbilityType.values()) {
            Identifier advancementId = buildAdvancementId(ability);
            consumer.accept(Advancement.Builder
                .createUntelemetered()
                .parent(rootAdvancement)
                .display(
                    ability.getIcon(),
                    ability.getTitle(),
                    ability.getDescription(),
                    null,
                    AdvancementFrame.TASK,
                    true,
                    false,
                    false
                )
                .rewards(AdvancementRewards.Builder.function(advancementId))
                .criterion(
                    DEMYSTIFIED_CRITERION_PREFIX + ability.getLowerCaseName(),
                    Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())
                )
                .criterion(
                    UNLOCKED_CRITERION,
                    Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())
                )
                .build(advancementId)
            );
        }
    }
}
