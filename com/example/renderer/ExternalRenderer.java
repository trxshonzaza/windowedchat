package com.example.renderer;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.util.List;

public class ExternalRenderer {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    private static boolean windowCreated = false;
    private static int width = 640;
    private static int height = 480;
    private static long window = 0;
    private static long mcWindow = 0;
    private static GLCapabilities mcCapabilities = null;
    private static GLCapabilities capabilities = null;
    private static int fbo = 0;
    private static int depthBuffer = 0;
    public static int colorBuffer = 0;
    private static int vao;

    public static void createWindow() {

        windowCreated = true;

        mcCapabilities = GL.getCapabilities();
        capabilities = GL.createCapabilities();

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        mcWindow = GLFW.glfwGetCurrentContext();
        window = GLFW.glfwCreateWindow(width, height, "WindowedChat", MemoryUtil.NULL, GLFW.glfwGetCurrentContext());

        GLFW.glfwSetWindowSize(window, width, height);
        GLFW.glfwMakeContextCurrent(window);

        GL.setCapabilities(capabilities);

        GLFW.glfwShowWindow(window);

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        GL11.glClearColor(0, 0, 0, 1.0f);

        GL.setCapabilities(mcCapabilities);
        GLFW.glfwMakeContextCurrent(mcWindow);

        attach();

    }

    public static void render(DrawContext context) {

        if(!windowCreated)
            createWindow();

        GLFW.glfwMakeContextCurrent(window);

        GL.setCapabilities(capabilities);

        bind();
        GlStateManager._viewport(0, 0, width, height);
        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

        int width = 200;
        int height = 100;
        int x = 10;
        int y = 10;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        if(buffer.isBuilding()) {

            buffer.end();
            buffer.endNullable();

        }

        // text

        String text = "i hate basketball people (joke)";
        int textColor = 0xFFFFFF; // White text color
        int textX = x + width / 2 - mc.textRenderer.getWidth(text) / 2;
        int textY = y + height / 2 - mc.textRenderer.fontHeight / 2;

        context.getMatrices().push();
        context.getMatrices().loadIdentity();
        context.drawText(mc.textRenderer, Text.literal(text), textX, textY, textColor, false);
        context.fill(0, 0, 20, 20, 0xFFFFFF);
        context.drawHorizontalLine(0, 10, 20, 0xFFFFFF);
        context.getMatrices().pop();

        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();

        GlStateManager._glBindFramebuffer((int) mcWindow, mc.getFramebuffer().fbo);

        GL11.glFinish();

        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        GlStateManager._viewport(0, 0, width, height);

        GL.setCapabilities(mcCapabilities);

        GLFW.glfwMakeContextCurrent(mcWindow);

    }

    public static void attach() {

        if (fbo == 0)
            fbo = GlStateManager.glGenFramebuffers();

        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, fbo);
        GL30.glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorBuffer, 0);
        GL30.glFramebufferRenderbuffer(GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_RENDERBUFFER, depthBuffer);

    }

    public static void bind() {

        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, fbo);

    }

}
