package com.wary.cc.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public class Line extends Shape {
    public final Vec3d start;
    public final Vec3d end;
    public final int color;
    public static final float THICKNESS = 2f;

    public Line(Vec3d start, Vec3d end, int color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    @Override
    public void addLines(Consumer<Line> lines, Camera camera, RenderTickCounter RenderTickCounter) {
        lines.accept(toCameraView(camera, RenderTickCounter, prevPos.subtract(getPos())));
    }

    public Line toCameraView(Camera camera, RenderTickCounter RenderTickCounter, Vec3d prevPosOffset) {
        float delta = RenderTickCounter.getFixedDeltaTicks();
        Vec3d cameraPos = camera.getCameraPos();
        return new Line(
                start.add(prevPosOffset.multiply(1 - delta)).subtract(cameraPos),
                end.add(prevPosOffset.multiply(1 - delta).subtract(cameraPos)),
                color
        );
    }

    public void draw(VertexConsumer vertexConsumer, MatrixStack MatrixStack) {
        Vec3d normal = this.end.subtract(this.start).normalize();
        putVertex(MatrixStack, vertexConsumer, this.start, normal);
        putVertex(MatrixStack, vertexConsumer, this.end, normal);
    }

    private void putVertex(MatrixStack MatrixStack, VertexConsumer vertexConsumer, Vec3d pos, Vec3d normal) {
        vertexConsumer.vertex(
                MatrixStack.peek().getPositionMatrix(),
                (float) pos.x,
                (float) pos.y,
                (float) pos.z
        ).color(
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F,
                1.0F
        ).normal(
                MatrixStack.peek(),
                (float) normal.x,
                (float) normal.y,
                (float) normal.z
        ).lineWidth(2);
    }

    @Override
    public Vec3d getPos() {
        return start;
    }
}