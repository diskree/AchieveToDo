package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.blocked_actions.BlockedActionCategory;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementToast.class)
public class AdvancementToastMixin {

    @Unique
    private static final Identifier ACTION_UNBLOCKED_TOAST_TEXTURE =
        Identifier.of(BuildConfig.MOD_ID, "action_unblocked_toast");

    @Unique
    private boolean isActionUnblockedSoundPlayed;

    @Unique
    private @Nullable BlockedActionCategory getBlockedActionCategory() {
        if (advancement == null) {
            return null;
        }
        BlockedActionType blockedAction = BlockedActionType.map(advancement.id());
        return blockedAction != null ? blockedAction.getCategory() : null;
    }

    @Shadow
    @Final
    private AdvancementEntry advancement;

    @ModifyArg(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
            ordinal = 0
        ),
        index = 0
    )
    private Identifier redirectBackgroundTexture(Identifier texture) {
        BlockedActionCategory category = getBlockedActionCategory();
        return category != null ? ACTION_UNBLOCKED_TOAST_TEXTURE : texture;
    }

    @ModifyArg(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"
        ),
        index = 1
    )
    private Text modifyTitle(Text text) {
        BlockedActionCategory category = getBlockedActionCategory();
        return category != null ? Text.translatable(category.getUnblockPopupTitle().getString()) : text;
    }

    @Inject(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawItemWithoutEntity(Lnet/minecraft/item/ItemStack;II)V",
            shift = At.Shift.BEFORE,
            ordinal = 0
        )
    )
    private void playActionUnblockedSound(
        DrawContext context,
        ToastManager manager,
        long startTime,
        CallbackInfoReturnable<Toast.Visibility> cir
    ) {
        if (!isActionUnblockedSoundPlayed && startTime > 0L && getBlockedActionCategory() != null) {
            isActionUnblockedSoundPlayed = true;
            manager.getClient().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.8f, 0.2f));
        }
    }
}
