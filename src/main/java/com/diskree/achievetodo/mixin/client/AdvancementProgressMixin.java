package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AchieveToDoClient;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import com.diskree.achievetodo.blocked_actions.datagen.AdvancementsGenerator;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AdvancementProgress.class)
public abstract class AdvancementProgressMixin {

    @Shadow
    private AdvancementRequirements requirements;

    @Unique
    private int getActionUnblockAdvancementsCount() {
        if (requirements.requirements().size() == 2) {
            List<String> criteria = requirements.requirements().getFirst();
            if (criteria != null && !criteria.isEmpty()) {
                String maybeDemystifiedCriterion = criteria.getFirst();
                String prefix = AdvancementsGenerator.BLOCKED_ACTION_DEMYSTIFIED_CRITERION_PREFIX;
                if (maybeDemystifiedCriterion != null && maybeDemystifiedCriterion.startsWith(prefix)) {
                    BlockedActionType blockedAction = BlockedActionType.map(maybeDemystifiedCriterion.split(prefix)[1]);
                    if (blockedAction != null) {
                        return blockedAction.getUnblockAdvancementsCount();
                    }
                }
            }
        }
        return -1;
    }

    @Inject(
        method = "getProgressBarPercentage",
        at = @At("HEAD"),
        cancellable = true
    )
    public void overrideBlockedActionProgress(CallbackInfoReturnable<Float> cir) {
        float actionUnblockAdvancementsCount = getActionUnblockAdvancementsCount();
        if (actionUnblockAdvancementsCount != -1) {
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer != null) {
                cir.setReturnValue(
                    Math.min(
                        actionUnblockAdvancementsCount,
                        AchieveToDoClient.advancementsCount / actionUnblockAdvancementsCount
                    )
                );
            }
        }
    }

    @Inject(
        method = "getProgressBarFraction",
        at = @At("HEAD"),
        cancellable = true
    )
    public void overrideBlockedActionProgressBarFraction(CallbackInfoReturnable<Text> cir) {
        int actionUnblockAdvancementsCount = getActionUnblockAdvancementsCount();
        if (actionUnblockAdvancementsCount != -1) {
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer != null) {
                cir.setReturnValue(Text.translatable(
                    "advancements.progress",
                    Math.min(
                        actionUnblockAdvancementsCount,
                        AchieveToDoClient.advancementsCount
                    ),
                    actionUnblockAdvancementsCount
                ));
            }
        }
    }
}
