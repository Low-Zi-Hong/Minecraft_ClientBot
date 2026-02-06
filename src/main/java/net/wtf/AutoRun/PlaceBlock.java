package net.wtf.AutoRun;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PlaceBlock {

    public static void PlaceBlock(BlockPos pos, Wocao.Callback callback)
    {
        new Thread(() -> {

            try {
                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;
                ClientPlayerInteractionManager interactionManager = new ClientPlayerInteractionManager(client,client.getNetworkHandler());

                BlockPos targetPos = pos;
                Vec3d hitPos = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
                BlockHitResult hitResult = new BlockHitResult(hitPos, Direction.UP,targetPos,false);

                interactionManager.interactBlock(player, Hand.MAIN_HAND,hitResult);
                Thread.sleep(100);
                callback.onComplete(true);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();


    }

}
