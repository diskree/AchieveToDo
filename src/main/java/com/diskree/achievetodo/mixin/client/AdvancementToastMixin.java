package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.BuildConfig;
import com.diskree.achievetodo.blocked_actions.BlockedActionCategory;
import com.diskree.achievetodo.blocked_actions.BlockedActionType;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementToast.class)
public class AdvancementToastMixin {

    @Unique
    private static final Identifier ACTION_UNBLOCKED_TOAST_TEXTURE =
        Identifier.of(BuildConfig.MOD_ID, "action_unblocked_toast");

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
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V",
            ordinal = 0
        ),
        index = 1
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

    @ModifyVariable(
        method = "draw",
        at = @At(value = "STORE"),
        ordinal = 0
    )
    private int setCustomColorForUnblockingTitle(int color) {
        return getBlockedActionCategory() != null ? Colors.BLACK : color;
    }

    @ModifyArg(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I",
            ordinal = 0
        ),
        index = 4
    )
    private int setCustomColorForBlockedActionDescription(int color) {
        return getBlockedActionCategory() != null ? 0x725e3c : color;
    }

    @Inject(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancement/AdvancementDisplay;getFrame()Lnet/minecraft/advancement/AdvancementFrame;",
            ordinal = 0
        )
    )
    private void playActionUnblockedSound(
        ToastManager manager,
        long time,
        CallbackInfo ci
    ) {
        if (getBlockedActionCategory() != null) {
            manager.getClient().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.8f, 0.2f)
            );
        }
    }
}
