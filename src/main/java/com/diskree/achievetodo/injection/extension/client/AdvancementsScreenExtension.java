package com.diskree.achievetodo.injection.extension.client;

import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.util.Identifier;

public interface AdvancementsScreenExtension {
    void achievetodo$setFocusedAdvancementWidget(AdvancementWidget focusedAdvancementWidget);

    void achievetodo$onMouseReleased(int button);

    Identifier achievetodo$getActiveAdvancementId();

    void achievetodo$setActiveAdvancementId(Identifier advancementId);
}
