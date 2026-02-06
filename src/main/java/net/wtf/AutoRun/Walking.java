package net.wtf.AutoRun;

import net.ClientInitialiser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Walking {
    public static void register()
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (ClientInitialiser.isMoving)
        {
            player.travel(new Vec3d(0,0,1));
        }

    }

    public static void WalkToBlock(BlockPos EndPos, boolean Jump, Wocao.Callback callback){
        if(ClientInitialiser.isMoving) return;

        MinecraftClient client = MinecraftClient.getInstance();

        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();

        new Thread(() -> {
            try {
                ClientInitialiser.isMoving = true;

                System.out.println(playerPos.toShortString() + " and " + EndPos.toShortString());

                while( client.player.getBlockPos().getX() != EndPos.getX() ||
                       client.player.getBlockPos().getY() != EndPos.getY() ||
                       client.player.getBlockPos().getZ() != EndPos.getZ())
                {
                    double playerX = client.player.getX();
                    double playerZ = client.player.getZ();

                    // Calculate the delta between the player's position and the target
                    double deltaX = (EndPos.getX() - playerX)+0.5;
                    double deltaZ = (EndPos.getZ() - playerZ)+0.5;

                    float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90);  // Rotation around Y-axis (horizontal)
                    MinecraftClient.getInstance().player.setAngles(yaw,client.player.getPitch());

                    Vec3d velocity = client.player.getVelocity();

                    Thread.sleep(40);

                    if(Jump && velocity.length() <= 0.08)
                    {
                        client.player.jump();
                    }
                    System.out.println(velocity.length());

                }
                Thread.sleep(40);
                System.out.println("Reach");
                ClientInitialiser.isMoving= false;
                callback.onComplete(true);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();


    }
}
