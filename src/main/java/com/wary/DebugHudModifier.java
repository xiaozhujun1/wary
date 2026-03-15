package com.wary;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.wary.ConfigGui.createConfigScreen;

public class DebugHudModifier implements ModInitializer {
	public static final String MOD_ID = "debughudmodifier";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient mc;
	public static List<ModifiedText> leftlist = new ArrayList<>();
	public static List<ModifiedText> rightlist = new ArrayList<>();
	public static int offsetX = 0;
	public static int offsetZ = 0;
	public static int spawnRadius = 10000;
	public static String playerX;
	public static String playerY;
	public static String playerZ;
	public static boolean disableInSpawn = false;
	public static boolean fakeCoordsEnabled = false;
	public static boolean testOption = false;
	public static Config config;

	private static KeyBinding keyBinding;

	public static String extractWithSplit(String str) {
		// 找到第一个数字的位置
		int firstDigitIndex = -1;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '-' || Character.isDigit(c)) {
				firstDigitIndex = i;
				break;
			}
		}

		if (firstDigitIndex == -1) {
			//System.out.println("未找到坐标");
			return "";
		}

		// 提取坐标部分
		String coordinatesPart = str.substring(firstDigitIndex);
		String[] coordStrings = coordinatesPart.split(",\\s*");

		if (coordStrings.length >= 3) {
			int x = Integer.parseInt(coordStrings[0].trim()) + offsetX;
			int y = Integer.parseInt(coordStrings[1].trim());
			int z = Integer.parseInt(coordStrings[2].trim()) + offsetZ;
			return x + ", " + y + ", " + z;
		}
		return "";
	}

	public static boolean nearSpawn() {
		if (mc.player == null) return false;
        return Math.abs(mc.player.getX()) < spawnRadius && Math.abs(mc.player.getZ()) < spawnRadius && disableInSpawn;
    }

	@Override
	public void onInitialize() {
		mc = MinecraftClient.getInstance();
		AutoConfig.register(Config.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(Config.class).getConfig();
		offsetX = config.offsetx;
		offsetZ = config.offsetz;
		disableInSpawn = config.disableinspawn;
		fakeCoordsEnabled = config.fakecoordsenabled;
		testOption = config.testoption;
		spawnRadius = config.spawnradius;

		ModifierCommands modifierCommands = new ModifierCommands();
		modifierCommands.initializeCommands();

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Open Config GUI",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				new KeyBinding.Category(Identifier.of("debughudmodifier"))
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                mc.setScreen(createConfigScreen(mc.currentScreen));
            }
        });
	}
}