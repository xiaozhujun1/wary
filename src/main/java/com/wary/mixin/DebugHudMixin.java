package com.wary.mixin;

import com.wary.DebugHudModifier;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

import static com.wary.DebugHudModifier.*;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V"))
    public void drawText(Args args) {
        List<String> text = args.get(1);
        String str;
        int blockX = 0, blockY = 0, blockZ = 0, chunkX = 0, chunkY = 0, chunkZ = 0;

        if (!fakeCoordsEnabled) return;

        if ((boolean) args.get(2)) {
            for (int i = 0; i < 50; i++) {
                text.add("");
            }
            for (com.wary.ModifiedText modifiedText : leftlist) {
                text.set(modifiedText.getLine() - 1, modifiedText.getText());
            }
            args.set(1,text);
        }else {
            for (int i = 0; i < 50; i++) {
                text.add("");
            }
            for (com.wary.ModifiedText modifiedText : rightlist) {
                text.set(modifiedText.getLine() - 1, modifiedText.getText());
            }
            args.set(1,text);
        }

        if (mc.player != null) {
            if (nearSpawn()) return;
            playerX = String.format("%.3f", (mc.player.getX() + offsetX));
            playerY = String.format("%.5f", mc.player.getY());
            playerZ = String.format("%.3f", (mc.player.getZ() + offsetZ));
            blockX = (int)mc.player.getX() + offsetX;
            blockY = (int)mc.player.getY();
            blockZ = (int)mc.player.getZ() + offsetZ;
            chunkX = blockX / 16;
            chunkY = blockY / 16;
            chunkZ = blockZ / 16;
        }
        for (int i = 0; i < text.size() && !nearSpawn(); i++) {
            str = text.get(i);
            if (str.startsWith("Targeted",2)) {
                if (str.contains("Block")) {
                    text.set(i, Formatting.UNDERLINE + "Targeted Block: " + extractWithSplit(str));
                }else if (str.contains("Fluid")){
                    text.set(i, Formatting.UNDERLINE + "Targeted Fluid: " + extractWithSplit(str));
                }else {return;}
            }
            if (str.startsWith("XYZ")) {
                text.set(i, "XYZ: "+ playerX + " / " + playerY + " / " + playerZ);
                text.set(i+1, "Block: " + blockX + " " + blockY + " " + blockZ);
                text.set(i+2, "Chunk: " + chunkX + " " + chunkY + " " + chunkZ + " [" + (chunkX % 32 + 32) % 32 + " " + (chunkZ % 32 + 32) % 32 + " in r." + (chunkX > 0 ? chunkX / 32 : chunkX / 32 - 1) + "." + (chunkZ > 0 ? chunkZ / 32 : chunkZ / 32 - 1) + ".mca]");
                text.set(i+5, "Section-relative: " + (blockX % 16 + 16) % 16 + " " + ((blockY % 16 + 16) % 16 + " " + (blockZ % 16 + 16) % 16));
            }
        }


    }
}