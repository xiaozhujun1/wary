package com.wary.cc.render;

import java.util.*;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;

public class RenderQueue {
    private static int tickCounter = 0;
    private static final List<AddQueueEntry> addQueue = new ArrayList<>();
    private static final List<RemoveQueueEntry> removeQueue = new ArrayList<>();
    private static final EnumMap<Layer, Map<Object, Shape>> queue = new EnumMap<>(Layer.class);

    private static final RenderStateDataKey<EnumMap<Layer, List<Line>>> LINES_KEY = RenderStateDataKey.create(() -> "clientcommands render queue");

    private static final RenderPipeline LINES_NO_DEPTH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
                    .withLocation(Identifier.of("qwq"))
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build()
    );
    public static final RenderLayer LINES_NO_DEPTH_LAYER = RenderLayer.of(
            "clientcommands_no_depth",
            RenderSetup.builder(LINES_NO_DEPTH_PIPELINE)
                    .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .outputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .build()
    );

    static {
        ClientTickEvents.START_CLIENT_TICK.register(RenderQueue::tick);

        MoreWorldRenderEvents.EXTRACT_STATE.register((state, camera, deltaTracker) -> {
            EnumMap<Layer, List<Line>> lines = new EnumMap<>(Layer.class);
            queue.forEach((layer, shapes) -> {
                List<Line> linesToRender = new ArrayList<>();
                shapes.values().forEach(shape -> shape.addLines(linesToRender::add, camera, deltaTracker));
                lines.put(layer, linesToRender);
            });
            state.setData(LINES_KEY, lines);
        });

        MoreWorldRenderEvents.END_MAIN_PASS.register((bufferSource, poseStack, state) -> {
            render(Layer.ON_TOP, bufferSource.getBuffer(LINES_NO_DEPTH_LAYER), poseStack, state);
        });
    }

    public static void register() {
        // load class
    }

    public static void add(Layer layer, Object key, Shape shape, int life) {
        addQueue.add(new AddQueueEntry(layer, key, shape, life));
    }



    public static void addCuboid(Layer layer, Object key, Box cuboid, int color, int life) {
        add(layer, key, new Cuboid(cuboid, color), life);
    }

    public static void addLine(Layer layer, Object key, Vec3d from, Vec3d to, int color, int life) {
        add(layer, key, new Line(from, to, color), life);
    }

    public static void remove(Layer layer, Object key) {
        removeQueue.add(new RemoveQueueEntry(layer, key));
    }

    private static void doAdd(AddQueueEntry entry) {
        Map<Object, Shape> shapes = queue.computeIfAbsent(entry.layer(), k -> new LinkedHashMap<>());
        Shape oldShape = shapes.get(entry.key());
        if (oldShape != null) {
            entry.shape().prevPos = oldShape.prevPos;
        } else {
            entry.shape().prevPos = entry.shape().getPos();
        }
        entry.shape().deathTime = tickCounter + entry.life();
        shapes.put(entry.key(), entry.shape());
    }

    private static void tick(MinecraftClient mc) {
        for (RemoveQueueEntry entry : removeQueue) {
            Map<Object, Shape> shapes = queue.get(entry.layer());
            if (shapes != null) {
                shapes.remove(entry.key());
            }
        }
        removeQueue.clear();

        queue.values().forEach(shapes -> shapes.values().forEach(shape -> shape.prevPos = shape.getPos()));
        tickCounter++;
        for (AddQueueEntry entry : addQueue) {
            doAdd(entry);
        }
        addQueue.clear();
        for (Map<Object, Shape> shapes : queue.values()) {
            Iterator<Shape> itr = shapes.values().iterator();
            while (itr.hasNext()) {
                Shape shape = itr.next();
                if (tickCounter == shape.deathTime) {
                    itr.remove();
                }
                shape.tick();
            }
        }
    }

    private static void render(Layer layer, VertexConsumer vertexConsumer, MatrixStack poseStack, WorldRenderState state) {
        EnumMap<Layer, List<Line>> lines = state.getData(LINES_KEY);
        if (lines == null) {
            return;
        }

        List<Line> linesToRender = lines.get(layer);
        if (linesToRender == null) {
            return;
        }

        for (Line line : linesToRender) {
            line.draw(vertexConsumer, poseStack);
        }
    }

    public enum Layer {
        ON_TOP
    }

    private record AddQueueEntry(Layer layer, Object key, Shape shape, int life) {}

    private record RemoveQueueEntry(Layer layer, Object key) {}
}