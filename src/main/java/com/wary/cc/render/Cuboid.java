package com.wary.cc.render;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public class Cuboid extends Shape {

    private final Line[] edges = new Line[12];
    public final Vec3d start;
    public final Vec3d size;

    public Cuboid(Box box, int color) {
        this(new Vec3d(box.minX, box.minY, box.minZ), new Vec3d(box.maxX, box.maxY, box.maxZ), color);
    }

    public Cuboid(Vec3d start, Vec3d end, int color) {
        this.start = start;
        this.size = new Vec3d(end.x - start.x, end.y - start.y, end.z - start.z);
        this.edges[0] = new Line(this.start, this.start.add(this.size.x, 0, 0), color);
        this.edges[1] = new Line(this.start, this.start.add(0, this.size.y, 0), color);
        this.edges[2] = new Line(this.start, this.start.add(0, 0, this.size.z), color);
        this.edges[3] = new Line(this.start.add(this.size.x, 0, this.size.z), this.start.add(this.size.x, 0, 0), color);
        this.edges[4] = new Line(this.start.add(this.size.x, 0, this.size.z), this.start.add(this.size.x, this.size.y, this.size.z), color);
        this.edges[5] = new Line(this.start.add(this.size.x, 0, this.size.z), this.start.add(0, 0, this.size.z), color);
        this.edges[6] = new Line(this.start.add(this.size.x, this.size.y, 0), this.start.add(this.size.x, 0, 0), color);
        this.edges[7] = new Line(this.start.add(this.size.x, this.size.y, 0), this.start.add(0, this.size.y, 0), color);
        this.edges[8] = new Line(this.start.add(this.size.x, this.size.y, 0), this.start.add(this.size.x, this.size.y, this.size.z), color);
        this.edges[9] = new Line(this.start.add(0, this.size.y, this.size.z), this.start.add(0, 0, this.size.z), color);
        this.edges[10] = new Line(this.start.add(0, this.size.y, this.size.z), this.start.add(0, this.size.y, 0), color);
        this.edges[11] = new Line(this.start.add(0, this.size.y, this.size.z), this.start.add(this.size.x, this.size.y, this.size.z), color);
    }

    @Override
    public void addLines(Consumer<Line> lines, Camera camera, RenderTickCounter deltaTracker) {
        Vec3d prevPosOffset = prevPos.subtract(getPos());
        for (Line edge : this.edges) {
            lines.accept(edge.toCameraView(camera, deltaTracker, prevPosOffset));
        }
    }

    @Override
    public Vec3d getPos() {
        return start;
    }

}