package com.diskree.achievetodo.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.AdvancementDisplays;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AdvancementDisplays.class)
public class AdvancementVisibilityEvaluatorMixin {

    @ModifyConstant(
        method = "shouldDisplay(Lit/unimi/dsi/fastutil/Stack;)Z",
        constant = @Constant(intValue = 2)
    )
    private static int forceShowAllAdvancements(
        int displayDepth,
        @Local(argsOnly = true) Stack<AdvancementDisplays.Status> statuses
    ) {
        return ((ObjectArrayList<AdvancementDisplays.Status>) statuses).size() - 1;
    }
}
