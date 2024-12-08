package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import com.diskree.achievetodo.blocked_actions.datagen.AdvancementsGenerator;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(PlacedAdvancement.class)
public abstract class PlacedAdvancementMixin {

    @Shadow
    public abstract AdvancementEntry getAdvancementEntry();

    @Shadow
    @Final
    private Set<PlacedAdvancement> children;

    @Shadow
    public abstract Advancement getAdvancement();

    @Inject(
        method = "getChildren",
        at = @At("HEAD"),
        cancellable = true
    )
    public void getChildrenInject(CallbackInfoReturnable<Iterable<PlacedAdvancement>> cir) {
        Identifier advancementId = getAdvancementEntry().id();
        if (advancementId.getNamespace().equals(BuildConfig.MOD_ID) && getAdvancement().isRoot()) {
            List<Identifier> rowsOrder = new ArrayList<>();
            for (BlockedActionType[] row : AdvancementsGenerator.TREE) {
                rowsOrder.add(AdvancementsGenerator.buildAdvancementId(row[0]));
            }
            List<PlacedAdvancement> childrenList = new ArrayList<>(children);
            childrenList.sort((placedAdvancement, otherPlacedAdvancement) -> {
                Integer index = rowsOrder.indexOf(placedAdvancement.getAdvancementEntry().id());
                Integer otherIndex = rowsOrder.indexOf(otherPlacedAdvancement.getAdvancementEntry().id());
                return index.compareTo(otherIndex);
            });
            cir.setReturnValue(childrenList);
        }
    }
}
