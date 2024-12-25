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

    public static final Block BACKGROUND = Blocks.PALE_MOSS_BLOCK;

    public static @NotNull List<List<AbilityType>> getAbilitiesTree() {
        Map<AbilitiesBranchType, List<AbilityType>> grouped = Arrays.stream(AbilityType.values())
            .collect(Collectors.groupingBy(AbilityType::getBranchType));
        for (Map.Entry<AbilitiesBranchType, List<AbilityType>> entry : grouped.entrySet()) {
            entry.getValue().sort(Comparator.comparing(AbilityType::getRequiredAdvancementsCount));
        }

        Map<AbilitiesBranchType, List<List<AbilityType>>> splittedMap = new HashMap<>();
        for (AbilitiesBranchType line : AbilitiesBranchType.values()) {
            List<AbilityType> abilities = grouped.getOrDefault(line, Collections.emptyList());
            int sublistCount = line.getSublistCount();
            if (sublistCount == 1) {
                splittedMap.put(line, Collections.singletonList(abilities));
            } else {
                List<List<AbilityType>> result = new ArrayList<>();
                int total = abilities.size();
                int chunkSize = (int) Math.ceil((double) total / sublistCount);
                int index = 0;
                while (index < total) {
                    int end = Math.min(index + chunkSize, total);
                    result.add(abilities.subList(index, end));
                    index += chunkSize;
                }
                while (result.size() < sublistCount) {
                    result.add(Collections.emptyList());
                }
                splittedMap.put(line, result);
            }
        }

        List<List<AbilityType>> left = new ArrayList<>();
        List<List<AbilityType>> center = new ArrayList<>();
        List<List<AbilityType>> right = new ArrayList<>();

        for (AbilitiesBranchType line : AbilitiesBranchType.values()) {
            List<List<AbilityType>> branch = splittedMap.get(line);
            int count = line.getSublistCount();
            if (count == 1) {
                center.addAll(branch);
            } else {
                int half = count / 2;
                List<List<AbilityType>> leftPart = new ArrayList<>(branch.subList(0, half));
                left.addAll(0, leftPart);
                List<List<AbilityType>> rightPart = branch.subList(half, count);
                right.addAll(rightPart);
            }
        }

        List<List<AbilityType>> result = new ArrayList<>();
        result.addAll(left);
        result.addAll(center);
        result.addAll(right);
        return result;
    }

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
                Identifier.ofVanilla("textures/block/" + Registries.BLOCK.getId(BACKGROUND).getPath() + ".png"),
                AdvancementFrame.TASK,
                false,
                false,
                false
            )
            .criterion("tick", TickCriterion.Conditions.createTick())
            .build(buildAdvancementId("root"));
        consumer.accept(rootAdvancement);

        AdvancementEntry parentAdvancement = rootAdvancement;
        for (List<AbilityType> branch : getAbilitiesTree()) {
            for (AbilityType ability : branch) {
                Identifier id = buildAdvancementId(ability);
                parentAdvancement = Advancement.Builder
                    .createUntelemetered()
                    .parent(parentAdvancement)
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
