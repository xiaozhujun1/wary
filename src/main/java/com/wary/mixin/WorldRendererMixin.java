package com.wary.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wary.cc.render.MoreWorldRenderEvents;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.wary.cc.render.MoreWorldRenderEvents.END_MAIN_PASS;
import static com.wary.cc.render.MoreWorldRenderEvents.EXTRACT_STATE;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private WorldRenderState worldRenderState;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;fillBlockBreakingProgressRenderState(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/state/WorldRenderState;)V", shift = At.Shift.AFTER))
    private void extractRenderStateEvent(
            CallbackInfo ci,
            @Local(argsOnly = true) Camera camera,
            @Local(argsOnly = true) RenderTickCounter deltaTracker,
            @Local Profiler profiler
    ) {
        profiler.swap("clientcommandsExtract");
        EXTRACT_STATE.invoker().extractState(this.worldRenderState, camera, deltaTracker);
    }

    @Inject(method = "method_62214", at = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V"))
    private void callEndMainPassEvent(
            CallbackInfo ci,
            @Local VertexConsumerProvider.Immediate bufferSource,
            @Local MatrixStack poseStack,
            @Local(argsOnly = true) WorldRenderState renderState,
            @Local(argsOnly = true) Profiler profiler
    ) {
        profiler.swap("clientcommandsEndMainPass");
        END_MAIN_PASS.invoker().endMainPass(bufferSource, poseStack, renderState);
    }
}