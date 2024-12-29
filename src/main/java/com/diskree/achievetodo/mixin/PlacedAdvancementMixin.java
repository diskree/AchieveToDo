package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AbilityType;
import com.diskree.achievetodo.AchieveToDo;
import com.diskree.achievetodo.gui.AdvancementsTab;
import com.diskree.achievetodo.injection.PlacedAdvancementImpl;
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

@Mixin(PlacedAdvancement.class)
public abstract class PlacedAdvancementMixin implements PlacedAdvancementImpl {

    @Unique
    private static final Map<String, List<String>> customChildrenOrderMap = new HashMap<>();

    @Unique
    private List<PlacedAdvancement> allAbilityAdvancements;

    public List<PlacedAdvancement> achievetodo$getAllAbilityAdvancements() {
        return allAbilityAdvancements;
    }

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
    }

    @Unique
    private List<PlacedAdvancement> sortedChildren = null;

    @Shadow
    @Final
    public Set<PlacedAdvancement> children;

    @Shadow
    @Final
    private AdvancementEntry advancementEntry;

    @Shadow
    public abstract PlacedAdvancement getRoot();

    @Inject(
        method = "getChildren",
        at = @At("HEAD"),
        cancellable = true
    )
    public void modifyChildren(CallbackInfoReturnable<Iterable<PlacedAdvancement>> cir) {
        if (sortedChildren != null) {
            cir.setReturnValue(sortedChildren);
            return;
        }

        PlacedAdvancement placedAdvancement = (PlacedAdvancement) (Object) this;
        Identifier advancementId = advancementEntry.id();
        AdvancementsTab tab = AdvancementsTab.findByAdvancement(advancementId);
        List<PlacedAdvancement> sortedChildren = new ArrayList<>(children);

        if (tab == AdvancementsTab.ABILITIES) {
            if (advancementEntry.value().isRoot()) {
                allAbilityAdvancements = children.stream().toList();
                sortedChildren.clear();
                List<List<AbilityType>> tree = AchieveToDo.buildAbilitiesTree();
                if (tree != null) {
                    for (List<AbilityType> row : tree) {
                        AbilityType firstAbility = row.getFirst();
                        for (PlacedAdvancement abilityAdvancement : children) {
                            if (AbilityType.findByAdvancement(abilityAdvancement) == firstAbility) {
                                sortedChildren.add(abilityAdvancement);
                            }
                        }
                    }
                }
            } else {
                sortedChildren.clear();
                if (getRoot() instanceof PlacedAdvancementImpl rootAdvancement) {
                    List<PlacedAdvancement> allAbilityAdvancements =
                        rootAdvancement.achievetodo$getAllAbilityAdvancements();
                    List<List<AbilityType>> tree = AchieveToDo.buildAbilitiesTree();
                    if (tree != null) {
                        for (List<AbilityType> row : tree) {
                            int index = row.indexOf(AbilityType.findByAdvancement(placedAdvancement));
                            if (index != -1 && index < row.size() - 1) {
                                AbilityType nextAbility = row.get(index + 1);
                                for (PlacedAdvancement advancement : allAbilityAdvancements) {
                                    if (AbilityType.findByAdvancement(advancement) == nextAbility) {
                                        sortedChildren.add(advancement);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (children.size() <= 1) {
                return;
            }
            if (customChildrenOrderMap.containsKey(advancementId.toString())) {
                List<String> childOrder = customChildrenOrderMap.get(advancementId.toString());
                List<String> actualChildIds = children.stream()
                    .map(child -> child.getAdvancementEntry().id().toString())
                    .toList();
                for (String childId : actualChildIds) {
                    if (!childOrder.contains(childId)) {
                        AchieveToDo.logger.error("Child ID '{}' is missing in childOrder for parent '{}'",
                            childId, advancementId
                        );
                        return;
                    }
                }
                List<String> filteredChildOrder = childOrder.stream()
                    .filter(actualChildIds::contains)
                    .toList();
                sortedChildren.sort(Comparator.comparingInt(child ->
                    filteredChildOrder.indexOf(child.getAdvancementEntry().id().toString())
                ));
            } else {
                sortedChildren.sort(Comparator.comparing(advancement ->
                    advancement.getAdvancementEntry().id()
                ));
            }
        }
        this.sortedChildren = sortedChildren;
        cir.setReturnValue(sortedChildren);
    }
}
