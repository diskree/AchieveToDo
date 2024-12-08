package com.diskree.achievetodo.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ErrorScreen extends Screen {

    private final Screen parent;
    private final Text message;

    public ErrorScreen(Screen parent, Text message) {
        super(Text.translatable("achievetodo.error_screen.title").formatted(Formatting.RED));
        this.message = message;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> close()).dimensions(width / 2 - 100, 180, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 90, 0xFFFFFF);
        context.drawWrappedText(textRenderer, message, width / 4, 110, width / 2, 0xFFFFFF, false);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
