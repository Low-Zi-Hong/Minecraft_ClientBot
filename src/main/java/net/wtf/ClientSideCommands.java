package net.wtf;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.ClientInitialiser;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.wtf.AutoRun.*;
import net.wtf.PathFinding.Astar;

public class ClientSideCommands {

    public static void register()
    {
        // Registering the client-side command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                registerCommands(dispatcher)
        );
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register
                (
                        ClientCommandManager.literal("wtf").then(
                                ClientCommandManager.literal("debug").then(
                                        ClientCommandManager.literal("HighlightPos")
                                                .then(ClientCommandManager.literal("Jump")
                                                        .executes(context -> {

                                                            Vec3d playerPos = context.getSource().getPlayer().getPos();
                                                            ClientInitialiser.blocKPosToRender.add(new Wocao.PathUnit((int) playerPos.getX(), (int) playerPos.getY(), (int) playerPos.getZ(), Astar.Direction.Up));


                                                            return 1;
                                                        })

                                                ).then(
                                                        ClientCommandManager.literal("Forward")
                                                                .executes(context -> {
                                                                    Vec3d playerPos = context.getSource().getPlayer().getPos();
                                                                    ClientInitialiser.blocKPosToRender.add(new Wocao.PathUnit((int) playerPos.getX(), (int) playerPos.getY(), (int) playerPos.getZ(), Astar.Direction.Forward));
                                                                    return 1;
                                                                })
                                                ).then(
                                                        ClientCommandManager.literal("Down")
                                                                .executes(context -> {
                                                                    Vec3d playerPos = context.getSource().getPlayer().getPos();
                                                                    ClientInitialiser.blocKPosToRender.add(new Wocao.PathUnit((int) playerPos.getX(), (int) playerPos.getY(), (int) playerPos.getZ(), Astar.Direction.Down));
                                                                    return 1;
                                                                })
                                                )

                                ).then(
                                        ClientCommandManager.literal("wtfAlgo").then(
                                                ClientCommandManager.argument("x", IntegerArgumentType.integer()).then(  // X coordinate
                                                        ClientCommandManager.argument("y", IntegerArgumentType.integer()).then(  // Y coordinate
                                                                ClientCommandManager.argument("z", IntegerArgumentType.integer())
                                                                        .then(ClientCommandManager.argument("go", BoolArgumentType.bool())
                                                                        .executes(context -> {
                                                                            // Get the FabricClientCommandSource
                                                                            FabricClientCommandSource source = context.getSource();

                                                                            // Extract block position arguments (x, y, z)
                                                                            int x = IntegerArgumentType.getInteger(context, "x");
                                                                            int y = IntegerArgumentType.getInteger(context, "y");
                                                                            int z = IntegerArgumentType.getInteger(context, "z");

                                                                            Boolean bo = BoolArgumentType.getBool(context,"go");

                                                                            ClientInitialiser.startPos = source.getPlayer().getBlockPos();
                                                                            ClientInitialiser.endPos = new BlockPos(x,y,z);

                                                                            Astar.Start();

                                                                            context.getSource().getPlayer().sendMessage(Text.literal("Use /wtf debug StartWocao"));

                                                                            return 1;
                                                                        })
                                                        )
                                                )
                                        )
                                        )


                                ).then(
                                        ClientCommandManager.literal("Astar").then(
                                                ClientCommandManager.argument("x", IntegerArgumentType.integer()).then(  // X coordinate
                                                        ClientCommandManager.argument("y", IntegerArgumentType.integer()).then(  // Y coordinate
                                                                ClientCommandManager.argument("z", IntegerArgumentType.integer())
                                                                        .executes(context -> {
                                                                            ClientInitialiser.blocKPosToRender.clear();
                                                                            BlockPos targetPos = new BlockPos(IntegerArgumentType.getInteger(context, "x"),IntegerArgumentType.getInteger(context, "y"),IntegerArgumentType.getInteger(context, "z"));
                                                                            context.getSource().getPlayer().sendMessage(Text.literal("targetPos set to " + targetPos.toShortString()));
                                                                            try {
                                                                                Astar.AstarAlgorithm(context.getSource().getPlayer().getBlockPos(),targetPos,false);
                                                                            } catch (InterruptedException e) {
                                                                                throw new RuntimeException(e);
                                                                            }


                                                                            return 1;
                                                                        })

                                                        )
                                                )
                                        )
                                ).then(
                                        ClientCommandManager.literal("StartWocao")
                                                .executes(context -> {

                                                    Wocao.StartOperation();

                                                    return 1;
                                                })
                                ).then(
                                        ClientCommandManager.literal("StopWocao")
                                                .executes(context -> {

                                                    Wocao.operationQueue.clear();

                                                    return 1;
                                                })

                                ).then(
                                        ClientCommandManager.literal("SendPacket").then(

                                                ClientCommandManager.literal("AllowFlying").then(
                                                        ClientCommandManager.argument("TruthValue", StringArgumentType.string())
                                                                .executes(context -> {
                                                                    // Update player abilities locally (client-side)
                                                                    MinecraftClient.getInstance().player.getAbilities().creativeMode = true;// Enable creative mode
                                                                    MinecraftClient.getInstance().player.getAbilities().flying = true;       // Allow flying

                                                                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdatePlayerAbilitiesC2SPacket(MinecraftClient.getInstance().player.getAbilities()));


                                                                    return 1;
                                                                })
                                                )
                                        ).then(

                                                ClientCommandManager.literal("DropItem")
                                                        .executes(context -> {
                                                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ITEM,MinecraftClient.getInstance().player.getBlockPos(), Direction.DOWN));
                                                            MinecraftClient.getInstance().player.playerScreenHandler.syncState();
                                                            return 1;
                                                        })
                                        )
                                        .then(
                                                ClientCommandManager.literal("Jump")
                                                        .executes(context -> {
                                                            MinecraftClient client = MinecraftClient.getInstance();
                                                            Walking.WalkToBlock(client.player.getBlockPos().add(1,1,0),true,callback ->{return;});
                                                            client.player.jump();
                                                            return 1;
                                                        })
                                        ).then(
                                                ClientCommandManager.literal("Move")
                                                        .executes(context -> {
                                                            // Assuming you are already inside a command or other appropriate context
                                                            MinecraftClient client = MinecraftClient.getInstance();
                                                            ClientPlayerEntity player = client.player;

                                                            Walking.WalkToBlock(player.getBlockPos().add(1,0,0),false,callback ->{return;});


                                                            return 1;
                                                        })
                                        ).then(
                                                ClientCommandManager.literal("Break")
                                                        .executes(context -> {
                                                            MinecraftClient client = MinecraftClient.getInstance();
                                                            ClientPlayerEntity player = MinecraftClient.getInstance().player;

                                                            ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client,client.getNetworkHandler());

                                                            BlockPos playerpos = player.getBlockPos();
                                                                // Start breaking the block
                                                            BreakBlock.setBreakBlock(playerpos.add(0,-1,0),callback ->{return;});

                                                            return 1;


                                                        })
                                        ).then(
                                                ClientCommandManager.literal("Place")
                                                        .executes(context -> {
                                                            PlaceBlock.PlaceBlock(MinecraftClient.getInstance().player.getBlockPos().add(1,0,0),callback ->{return;});
                                                            return 1;
                                                        })

                                        ).then(
                                                ClientCommandManager.literal("JumpnPut")
                                                        .executes(context -> {
                                                            JumpnPut.JumpAndPutBlock(MinecraftClient.getInstance().player.getBlockPos().add(0,1,0),callback ->{return;});
                                                            return 1;
                                                        })

                                                )
                                )


                        )










                );

    }

}
