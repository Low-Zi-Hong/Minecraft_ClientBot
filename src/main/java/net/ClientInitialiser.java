package net;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.wtf.AutoRun.PlaceBlock;
import net.wtf.AutoRun.Walking;
import net.wtf.AutoRun.Wocao;
import net.wtf.PathFinding.Astar;
import net.wtf.RenderingSystem.BlockRender;
import net.wtf.ClientSideCommands;
import net.wtf.RenderingSystem.RenderLine;

import java.util.ArrayList;
import java.util.List;

public class ClientInitialiser implements ClientModInitializer {

    //Variables
    //Rendering path coor list
    public static List<Wocao.PathUnit> blocKPosToRender = new ArrayList<>();

    //path Finding
    public static Boolean runingWtfAlgo = false;
    public static BlockPos startPos;
    public static BlockPos tempEndPos;
    public static BlockPos endPos;

    //movement control
    public static boolean isMoving = false;

    @Override
    public void onInitializeClient() {
        // Registering the client-side command
        ClientSideCommands.register();

        //Rendering System
        WorldRenderEvents.LAST.register(context -> {
                    BlockRender.register(context);
                    RenderLine.register(context);
                });

        //Path Finding Algorithm
        ClientTickEvents.END_WORLD_TICK.register(context ->{
            //track player position
            BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();

            if(runingWtfAlgo)
            {

                //check if player arrive the TempEndPoint
                if (Astar.checkReachEnd(playerPos,tempEndPos)) {

                    MinecraftClient.getInstance().player.sendMessage(Text.literal("renewing path"),true);
                    tempEndPos = null;
                    blocKPosToRender.clear();
                    Astar.Start();
                }

            }
            //check if player arrive at the End point
            if(playerPos.equals(endPos))
            {
                blocKPosToRender.clear();
                endPos = null;
                startPos = null;

                MinecraftClient.getInstance().player.sendMessage(Text.literal("your arrived at ur destination"),true);
            }

        });

        //Control player movement
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client,client.getNetworkHandler());
            //move
            Walking.register();

        });


    }
}
