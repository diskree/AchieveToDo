package com.diskree.achievetodo.mixin.client;

import com.diskree.achievetodo.AchieveToDoClient;
import com.diskree.achievetodo.gui.CreateWorldTab;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.Tab;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = CreateWorldScreen.class, priority = 500)
public abstract class CreateWorldScreenMixin {

    @ModifyArgs(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/widget/TabNavigationWidget$Builder;tabs([Lnet/minecraft/client/gui/tab/Tab;)Lnet/minecraft/client/gui/widget/TabNavigationWidget$Builder;"
        )
    )
    private void addModTab(@NotNull Args args) {
        CreateWorldScreen createWorldScreen = (CreateWorldScreen) (Object) this;
        Tab[] originalTabs = args.get(0);
        Tab[] newTabs = new Tab[originalTabs.length + 1];
        if (originalTabs.length >= 0) {
            System.arraycopy(originalTabs, 0, newTabs, 0, originalTabs.length);
        }
        AchieveToDoClient.createWorldTab = new CreateWorldTab(createWorldScreen);
        newTabs[originalTabs.length] = AchieveToDoClient.createWorldTab;
        args.set(0, newTabs);
    }
}
