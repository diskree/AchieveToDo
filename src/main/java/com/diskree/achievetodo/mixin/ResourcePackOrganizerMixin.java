package com.diskree.achievetodo.mixin;

import com.diskree.achievetodo.AchieveToDoMod;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(ResourcePackOrganizer.class)
public class ResourcePackOrganizerMixin {

    @Shadow
    @Final
    List<ResourcePackProfile> disabledPacks;

    @Shadow
    @Final
    List<ResourcePackProfile> enabledPacks;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initInject(Runnable updateCallback, Function iconIdSupplier, ResourcePackManager resourcePackManager, Consumer applier, CallbackInfo ci) {
        hideInternalDatapacks();
    }

    @Inject(method = "refresh", at = @At("RETURN"))
    public void refreshInject(CallbackInfo ci) {
        hideInternalDatapacks();
    }

    private void hideInternalDatapacks() {
        List<ResourcePackProfile> newEnabledPacks = new ArrayList<>();
        for (ResourcePackProfile pack : enabledPacks) {
            if (!AchieveToDoMod.isInternalDatapack(pack.getName())) {
                newEnabledPacks.add(pack);
            }
        }
        enabledPacks.clear();
        enabledPacks.addAll(newEnabledPacks);
        List<ResourcePackProfile> newDisabledPacks = new ArrayList<>();
        for (ResourcePackProfile pack : disabledPacks) {
            if (!AchieveToDoMod.isInternalDatapack(pack.getName())) {
                newDisabledPacks.add(pack);
            }
        }
        disabledPacks.clear();
        disabledPacks.addAll(newDisabledPacks);
    }
}
