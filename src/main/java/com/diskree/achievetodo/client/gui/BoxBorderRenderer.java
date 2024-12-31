package com.diskree.achievetodo.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;

public class BoxBorderRenderer {

    private static final Identifier FORCEFIELD_TEXTURE = Identifier.ofVanilla("textures/misc/forcefield.png");

    public static void renderBoxBorder(
        MatrixStack matrices,
        @NotNull Box box,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        RenderLayer renderLayer = RenderLayer.getWorldBorder(false);
        renderLayer.startDrawing();
        RenderSystem.setShaderTexture(0, FORCEFIELD_TEXTURE);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        float time = (System.currentTimeMillis() % 500) / 500F;
        BufferBuilder buffer =
            Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        drawFloorCeilingFace(buffer, matrices, box.minX, box.minZ, box.minY, box.maxX, box.maxZ, time);
        drawFloorCeilingFace(buffer, matrices, box.minX, box.minZ, box.maxY, box.maxX, box.maxZ, time);
        drawLeftRightFace(buffer, matrices, box.minX, box.minY, box.minZ, box.maxY, box.maxZ, time);
        drawLeftRightFace(buffer, matrices, box.maxX, box.minY, box.minZ, box.maxY, box.maxZ, time);
        drawFrontBackFace(buffer, matrices, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, time);
        drawFrontBackFace(buffer, matrices, box.minX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, time);
        BuiltBuffer builtBuffer = buffer.endNullable();
        if (builtBuffer != null) {
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        }
        renderLayer.endDrawing();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void drawFrontBackFace(
        @NotNull BufferBuilder buffer,
        @NotNull MatrixStack matrices,
        double x1,
        double y1,
        double z1,
        double x2,
        double y2,
        double z2,
        float time
    ) {
        MatrixStack.Entry entry = matrices.peek();
        float repeat = 0.3F;
        float u2 = time + (float) (Math.abs(x2 - x1) * repeat);
        float v1 = 0.0F;
        float v2 = (float) (Math.abs(y2 - y1) * repeat);
        buffer.vertex(entry.getPositionMatrix(), (float) x1, (float) y1, (float) z1).texture(time, v1);
        buffer.vertex(entry.getPositionMatrix(), (float) x2, (float) y1, (float) z1).texture(u2, v1);
        buffer.vertex(entry.getPositionMatrix(), (float) x2, (float) y2, (float) z2).texture(u2, v2);
        buffer.vertex(entry.getPositionMatrix(), (float) x1, (float) y2, (float) z2).texture(time, v2);
    }

    private static void drawFloorCeilingFace(
        @NotNull BufferBuilder buffer,
        @NotNull MatrixStack matrices,
        double x1,
        double z1,
        double y,
        double x2,
        double z2,
        float time
    ) {
        MatrixStack.Entry entry = matrices.peek();
        float repeat = 0.3F;
        float u2 = time + (float) (Math.abs(x2 - x1) * repeat);
        float v2 = time + (float) (Math.abs(z2 - z1) * repeat);
        buffer.vertex(entry.getPositionMatrix(), (float) x1, (float) y, (float) z1).texture(time, time);
        buffer.vertex(entry.getPositionMatrix(), (float) x2, (float) y, (float) z1).texture(u2, time);
        buffer.vertex(entry.getPositionMatrix(), (float) x2, (float) y, (float) z2).texture(u2, v2);
        buffer.vertex(entry.getPositionMatrix(), (float) x1, (float) y, (float) z2).texture(time, v2);
    }

    private static void drawLeftRightFace(
        @NotNull BufferBuilder buffer,
        @NotNull MatrixStack matrices,
        double x,
        double y1,
        double z1,
        double y2,
        double z2,
        float time
    ) {
        MatrixStack.Entry entry = matrices.peek();
        float repeat = 0.3F;
        float u2 = time + (float) (Math.abs(z2 - z1) * repeat);
        float v1 = 0.0F;
        float v2 = (float) (Math.abs(y2 - y1) * repeat);
        buffer.vertex(entry.getPositionMatrix(), (float) x, (float) y1, (float) z1).texture(time, v1);
        buffer.vertex(entry.getPositionMatrix(), (float) x, (float) y2, (float) z1).texture(time, v2);
        buffer.vertex(entry.getPositionMatrix(), (float) x, (float) y2, (float) z2).texture(u2, v2);
        buffer.vertex(entry.getPositionMatrix(), (float) x, (float) y1, (float) z2).texture(u2, v1);
    }
}
