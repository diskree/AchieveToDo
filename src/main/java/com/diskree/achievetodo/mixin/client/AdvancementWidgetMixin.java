package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import com.diskree.achievetodo.blocked_actions.datagen.AdvancementsGenerator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {

    @Unique
    private static final Identifier MYSTIFIED_TEXTURE = Identifier.of(BuildConfig.MOD_ID, "mystified_mark");

    @Unique
    private boolean isMystifiedBlockedActionAdvancement() {
        BlockedActionType blockedAction = BlockedActionType.map(advancement);
        if (blockedAction == null || blockedAction.isUnblocked(client.player) || progress == null) {
            return false;
        }
        CriterionProgress demystifiedProgress = progress.getCriterionProgress(
            AdvancementsGenerator.BLOCKED_ACTION_DEMYSTIFIED_CRITERION_PREFIX + blockedAction.getName()
        );
        return demystifiedProgress != null && !demystifiedProgress.isObtained();
    }

    @Shadow
    @Final
    private PlacedAdvancement advancement;

    @Shadow
    private @Nullable AdvancementProgress progress;

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyArg(
        method = "renderWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"
        ),
        index = 1
    )
    private Identifier renderWidgetsModifyIcon(Identifier texture) {
        return isMystifiedBlockedActionAdvancement() ? MYSTIFIED_TEXTURE : texture;
    }

    @WrapOperation(
        method = "renderWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawItemWithoutEntity(Lnet/minecraft/item/ItemStack;II)V"
        )
    )
    private void disableIconForMystifiedBlockedAction(
        DrawContext instance, ItemStack stack, int x, int y, Operation<Void> original
    ) {
        if (!isMystifiedBlockedActionAdvancement()) {
            original.call(instance, stack, x, y);
        }
    }

    @Inject(
        method = "shouldRender",
        at = @At("RETURN"),
        cancellable = true
    )
    private void disableTooltipForMystifiedBlockedAction(
        int originX,
        int originY,
        int mouseX,
        int mouseY,
        @NotNull CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(cir.getReturnValue() && !isMystifiedBlockedActionAdvancement());
    }

    @Redirect(
        method = "getProgressWidth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/AdvancementRequirements;getLength()I"
        )
    )
    public int initRedirect(AdvancementRequirements requirements) {
        BlockedActionType blockedAction = BlockedActionType.map(advancement);
        if (blockedAction != null) {
            return blockedAction.getUnblockAdvancementsCount();
        }
        return advancement.getAdvancement().requirements().getLength();
    }
}
