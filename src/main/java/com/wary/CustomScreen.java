package com.wary;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

public class CustomScreen extends Screen {
    private final Screen parent;

    public CustomScreen(Screen parent) {
        super(Text.literal("Custom Screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // 移除所有组件，为操作菜单做准备
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // 绘制半透明灰色背景
        int menuWidth = 400;
        int menuHeight = 250;
        int x = (this.width - menuWidth) / 2;
        int y = (this.height - menuHeight) / 2;
        int cornerRadius = 8;
        
        // 绘制半透明灰色背景
        context.fill(x, y, x + menuWidth, y + menuHeight, 0x80444444);
        
        // 绘制边框
        int borderWidth = 2;
        context.fill(x - borderWidth, y - borderWidth, x + menuWidth + borderWidth, y, 0xAA888888);
        context.fill(x - borderWidth, y + menuHeight, x + menuWidth + borderWidth, y + menuHeight + borderWidth, 0xAA888888);
        context.fill(x - borderWidth, y, x, y + menuHeight, 0xAA888888);
        context.fill(x + menuWidth, y, x + menuWidth + borderWidth, y + menuHeight, 0xAA888888);
        
        // 绘制测试三角形



    }


    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}