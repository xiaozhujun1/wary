package com.wary;

import com.wary.cc.render.RenderQueue;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VaultBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

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
	public static boolean portalScanner = false;
	public static boolean trialSpawnerScanner = false;
	public static boolean ominousVaultScanner = false;
	public static boolean chestScanner = false;
	public static Config config;

	private static KeyBinding keyBinding;
    private static KeyBinding customScreenKeyBinding;

	public static final CopyOnWriteArraySet<BlockPos> detectedPortals = new CopyOnWriteArraySet<>();
	public static final CopyOnWriteArraySet<BlockPos> detectedTrialSpawners = new CopyOnWriteArraySet<>();
	public static final CopyOnWriteArraySet<BlockPos> detectedOminousVaults = new CopyOnWriteArraySet<>();
	public static final CopyOnWriteArraySet<BlockPos> openedOminousVaults = new CopyOnWriteArraySet<>();
	public static final CopyOnWriteArraySet<BlockPos> detectedChests = new CopyOnWriteArraySet<>();

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

	private List<BlockPos> scanChunkForTargetBlock(World world, Chunk chunk) {
		// 用于记录所有 portal 方块
		List<BlockPos> portalPositions = new ArrayList<>();
		List<BlockPos> trialSpawnerPositions = new ArrayList<>();
		List<BlockPos> ominousVaultPositions = new ArrayList<>();
		List<BlockPos> chestPositions = new ArrayList<>();

		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;

		int minY = world.getBottomY();
		int maxY = 320;

		// 1. 收集当前 chunk 中所有 portal 方块
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = minY; y < maxY; y++) {
					BlockPos pos = new BlockPos(chunkX * 16 + x, y, chunkZ * 16 + z);

                    if (world.getBlockState(pos).isOf(Blocks.NETHER_PORTAL)) {
                        if (world.getBlockState(pos.add(0,1,0)).isOf(Blocks.NETHER_PORTAL)
                                && world.getBlockState(pos.add(0,2,0)).isOf(Blocks.NETHER_PORTAL)
                                && world.getBlockState(pos.down(1)).isOf(Blocks.OBSIDIAN)) {
                            portalPositions.add(pos.toImmutable());
							if (portalScanner) {
								mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.GREEN + "[Portal Scanner] " + Formatting.RESET + "Found Nether Portal on: (" + Formatting.UNDERLINE + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + Formatting.RESET + ")"));
							}
                        }
                    }

					if (world.getBlockState(pos).isOf(Blocks.TRIAL_SPAWNER)) {
						if (world.getBlockState(pos.add(1,-1,0)).isOf(Blocks.WAXED_CHISELED_COPPER)) {
							trialSpawnerPositions.add(pos.toImmutable());
						}
					}

					if (world.getBlockState(pos).isOf(Blocks.VAULT)) {
						if (world.getBlockState(pos.add(1,1,1)).isOf(Blocks.RED_GLAZED_TERRACOTTA) || world.getBlockState(pos.add(-1,1,-1)).isOf(Blocks.RED_GLAZED_TERRACOTTA)) {
							ominousVaultPositions.add(pos.toImmutable());
						}
					}

					if (world.getBlockState(pos).isOf(Blocks.CHEST)) {
						if (world.getBlockState(pos.add(0,1,0)).isOf(Blocks.AIR)) {
							chestPositions.add(pos.toImmutable());
						}
					}
                }
			}
		}

//		if (portalPositions.isEmpty()) {
//			return;
//		}
		detectedPortals.addAll(portalPositions);
		detectedTrialSpawners.addAll(trialSpawnerPositions);
		detectedOminousVaults.addAll(ominousVaultPositions);
		detectedChests.addAll(chestPositions);
		return portalPositions;
	}

	private void onRender(WorldRenderContext context) {
		for(BlockPos pos : detectedPortals) {
			if (!portalScanner) break;
			glowBlock(pos, null, 1, 0x800080);
		}
		for(BlockPos pos : detectedTrialSpawners) {
			if (!trialSpawnerScanner) break;
			glowBlock(pos, null, 1, 0xFFFF00);
		}
		for(BlockPos pos : detectedOminousVaults) {
			if (!ominousVaultScanner) break;
			if (openedOminousVaults.contains(pos)) {
				glowBlock(pos, null, 1, 0xEE0000);
			}else {
				glowBlock(pos, null, 1, 0x66CCFF);
			}
		}
		for(BlockPos pos : detectedChests) {
			if (!chestScanner) break;
			glowBlock(pos, null, 1, 0x008000);
		}
	}

	public static int glowBlock(BlockPos pos1, BlockPos pos2, int seconds, int color) {
		List<Box> boundingBoxes = new ArrayList<>();

		if (pos2 == null) {
			boundingBoxes.addAll(mc.world.getBlockState(pos1).getOutlineShape(mc.player.getEntityWorld(), pos1).getBoundingBoxes());
			if (boundingBoxes.isEmpty()) {
				boundingBoxes.add(new Box(pos1));
			} else {
				boundingBoxes.replaceAll((box) -> box.offset(pos1));
			}
		} else {
			boundingBoxes.add(Box.enclosing(pos1, pos2));
		}

		for (Box box : boundingBoxes) {
			RenderQueue.addCuboid(RenderQueue.Layer.ON_TOP, "glow_block_" + box.toString(), box, color, seconds * 20);
		}

		return boundingBoxes.size();
	}

	//for test command
	public static int glowBlock(FabricClientCommandSource source, BlockPos pos1, BlockPos pos2, int seconds, int color) {
		List<Box> boundingBoxes = new ArrayList<>();

		if (pos2 == null) {
			boundingBoxes.addAll(mc.world.getBlockState(pos1).getOutlineShape(source.getWorld(), pos1).getBoundingBoxes());
			if (boundingBoxes.isEmpty()) {
				boundingBoxes.add(new Box(pos1));
			} else {
				boundingBoxes.replaceAll((box) -> box.offset(pos1));
			}
		} else {
			boundingBoxes.add(Box.enclosing(pos1, pos2));
		}

		for (Box box : boundingBoxes) {
			RenderQueue.addCuboid(RenderQueue.Layer.ON_TOP, "glow_block_" + box.toString(), box, color, seconds * 20);
		}

		return boundingBoxes.size();
	}

	@Override
	public void onInitialize() {
		mc = MinecraftClient.getInstance();
		// 注册渲染队列
		RenderQueue.register();
		AutoConfig.register(Config.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(Config.class).getConfig();
		offsetX = config.offsetx;
		offsetZ = config.offsetz;
		disableInSpawn = config.disableinspawn;
		fakeCoordsEnabled = config.fakecoordsenabled;
		portalScanner = config.portalScanner;
		spawnRadius = config.spawnradius;
		trialSpawnerScanner = config.trialSpawnerScanner;
		ominousVaultScanner = config.ominousVaultScanner;
		chestScanner = config.chestScanner;

		ModifierCommands modifierCommands = new ModifierCommands();
		modifierCommands.initializeCommands();

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Open Config GUI",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				new KeyBinding.Category(Identifier.of("debughudmodifier"))
		));

        // 注册自定义屏幕快捷键
        customScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Custom Screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                new KeyBinding.Category(Identifier.of("debughudmodifier"))
        ));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                mc.setScreen(createConfigScreen(mc.currentScreen));
            }
            while (customScreenKeyBinding.wasPressed()) {
                mc.setScreen(new CustomScreen(mc.currentScreen));
            }
        });

		ClientChunkEvents.CHUNK_LOAD.register(this::scanChunkForTargetBlock);


		WorldRenderEvents.AFTER_ENTITIES.register(this::onRender);

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			BlockState state = mc.world.getBlockState(hitResult.getBlockPos());
			if (!state.isOf(Blocks.VAULT)) return ActionResult.PASS;
			if (!state.get(VaultBlock.OMINOUS)) return ActionResult.PASS;
			openedOminousVaults.add(hitResult.getBlockPos());
			return ActionResult.PASS;
		});

		ClientPlayConnectionEvents.JOIN.register(((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
			detectedPortals.clear();
			detectedTrialSpawners.clear();
			detectedOminousVaults.clear();
			detectedChests.clear();
		}));
	}
}