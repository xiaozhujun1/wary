package com.wary.cc.render;

import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Camera;

import java.util.function.Consumer;


public abstract class Shape {
    int deathTime;
    protected Vec3d prevPos;

    public void tick() {
    }

    public abstract void addLines(Consumer<Line> lines, Camera camera, RenderTickCounter deltaTracker);

    public abstract Vec3d getPos();

}