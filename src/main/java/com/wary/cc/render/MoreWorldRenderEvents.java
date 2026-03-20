package com.wary.cc.render;

import net.minecraft.client.util.math.MatrixStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.state.WorldRenderState;

public final class MoreWorldRenderEvents {
    private MoreWorldRenderEvents() {
    }

    public static final Event<ExtractState> EXTRACT_STATE = EventFactory.createArrayBacked(ExtractState.class, listeners -> (state, camera, deltaTracker) -> {
        for (ExtractState listener : listeners) {
            listener.extractState(state, camera, deltaTracker);
        }
    });

    public static final Event<EndMainPass> END_MAIN_PASS = EventFactory.createArrayBacked(EndMainPass.class, listeners -> (bufferSource, poseStack, state) -> {
        for (EndMainPass listener : listeners) {
            listener.endMainPass(bufferSource, poseStack, state);
        }
    });

    @FunctionalInterface
    public interface ExtractState {
        void extractState(WorldRenderState state, Camera camera, RenderTickCounter deltaTracker);
    }

    @FunctionalInterface
    public interface EndMainPass {
        void endMainPass(VertexConsumerProvider.Immediate bufferSource, MatrixStack matrixStack, WorldRenderState state);
    }
}