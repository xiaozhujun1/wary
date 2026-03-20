package com.wary;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import static com.wary.ConfigGui.createConfigScreen;
import static com.wary.DebugHudModifier.*;

public class ModifierCommands {
    public void initializeCommands() {
        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(ClientCommandManager.literal("debugmodifier")
                .then(ClientCommandManager.literal("left")
                        .then(ClientCommandManager.argument("line", IntegerArgumentType.integer())
                                .then(ClientCommandManager.argument("context", StringArgumentType.string())
                                        .executes(commandContext -> {
                                            mc.inGameHud.getChatHud().addMessage(Text.of("Successfully changed to " + StringArgumentType.getString(commandContext, "context")));
                                            leftlist.add(new ModifiedText(IntegerArgumentType.getInteger(commandContext, "line"),StringArgumentType.getString(commandContext, "context")));
                                            return 1;
                                        })
                                        .then(ClientCommandManager.literal("underlined")
                                                .executes(commandContext -> {
                                                    mc.inGameHud.getChatHud().addMessage(Text.of("Successfully changed to " + Formatting.UNDERLINE + StringArgumentType.getString(commandContext, "context")));
                                                    leftlist.add(new ModifiedText(IntegerArgumentType.getInteger(commandContext, "line"),String.valueOf(Formatting.UNDERLINE) + StringArgumentType.getString(commandContext, "context")));
                                                    return 1;
                                                })
                                        )
                                ))

                )
                .then(ClientCommandManager.literal("right")
                        .then(ClientCommandManager.argument("line", IntegerArgumentType.integer())
                                .then(ClientCommandManager.argument("context", StringArgumentType.string())
                                        .executes(commandContext -> {
                                            mc.inGameHud.getChatHud().addMessage(Text.of("Successfully changed to " + StringArgumentType.getString(commandContext, "context")));
                                            rightlist.add(new ModifiedText(IntegerArgumentType.getInteger(commandContext, "line"),StringArgumentType.getString(commandContext, "context")));
                                            return 1;
                                        })
                                        .then(ClientCommandManager.literal("underlined")
                                                .executes(commandContext -> {
                                                    mc.inGameHud.getChatHud().addMessage(Text.of("Successfully changed to " + Formatting.UNDERLINE + StringArgumentType.getString(commandContext, "context")));
                                                    rightlist.add(new ModifiedText(IntegerArgumentType.getInteger(commandContext, "line"), Formatting.UNDERLINE + StringArgumentType.getString(commandContext, "context")));
                                                    return 1;
                                                })
                                        )
                                ))

                )
                .then(ClientCommandManager.literal("clear")
                        .executes(commandContext -> {
                            leftlist.clear();
                            rightlist.clear();
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("setcoordsoffset")
                        .then(ClientCommandManager.argument("offsetX", IntegerArgumentType.integer())
                                .then(ClientCommandManager.argument("offsetY", IntegerArgumentType.integer())
                                        .executes(commandContext -> {
                                            offsetX = IntegerArgumentType.getInteger(commandContext, "offsetX");
                                            offsetZ = IntegerArgumentType.getInteger(commandContext, "offsetY");
                                            return 1;
                                        })
                                )
                        )
                )
                .then(ClientCommandManager.literal("disablenearspawn")
                        .executes(commandContext -> {
                            if (disableInSpawn) {
                                disableInSpawn = false;
                                mc.inGameHud.getChatHud().addMessage(Text.of("Auto disable when near the spawn: false"));
                            }else {
                                disableInSpawn = true;
                                mc.inGameHud.getChatHud().addMessage(Text.of("Auto disable when near the spawn: true"));
                            }
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("test")
                        .executes(commandContext -> {
                            glowBlock(commandContext.getSource(), new BlockPos(1,1,1), null,1, 0xffffff);
                            return 1;
                        })
                )
        )));
    }
}
