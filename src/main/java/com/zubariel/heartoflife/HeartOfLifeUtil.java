package com.zubariel.heartoflife;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;


public final class HeartOfLifeUtil {

    private HeartOfLifeUtil() {

    }
    public static boolean isShiftDown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }

        long handle = client.getWindow().getHandle();

        boolean isLeftShiftDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT) != GLFW.GLFW_RELEASE;
        boolean isRightShiftDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) != GLFW.GLFW_RELEASE;

        return isLeftShiftDown || isRightShiftDown;
    }
}