package net.wtf.AutoRun;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.BlockPos;

public class JumpnPut {

    public static void JumpAndPutBlock(BlockPos pos, Wocao.Callback callback)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        new Thread(() -> {
            try {
                player.jump();

                boolean putBlockAlr = false;
                while(!putBlockAlr)
                {
                    if (player.getVelocity().y <= 0.005)
                    {
                        PlaceBlock.PlaceBlock(pos.add(0,-1,0),success -> {
                            if (success) {
                                // Block placed successfully, notify JumpAndPutBlock callback
                                callback.onComplete(true);
                            } else {
                                // Failed to place block, notify JumpAndPutBlock callback
                                callback.onComplete(false);
                            }
                        });

                        putBlockAlr = true;
                    }
                    //System.out.println(player.getVelocity().y);
                    Thread.sleep(2);
                }
                callback.onComplete(true);

            } catch (InterruptedException e) {
                callback.onComplete(false);
                throw new RuntimeException(e);
            }
        }).start();
    }

}
