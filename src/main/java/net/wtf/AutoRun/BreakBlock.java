package net.wtf.AutoRun;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.concurrent.atomic.AtomicBoolean;

public class BreakBlock {

    // Use AtomicBoolean to handle thread safety
    private static AtomicBoolean isBreaking = new AtomicBoolean(false);

    public static void setBreakBlock(BlockPos pos, Wocao.Callback callback) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Start a new thread to handle the block-breaking process
        new Thread(() -> {
            try {
                // Wait for interactionManager to be initialized (non-null)
                while (client.interactionManager == null) {
                    Thread.sleep(50); // Sleep for 50ms before checking again
                }

                // Once interactionManager is non-null, proceed with breaking the block
                if (client.interactionManager.attackBlock(pos, Direction.DOWN) && !isBreaking.get()) {
                    System.out.println("Started breaking block.");
                    isBreaking.set(true); // Mark that we're breaking a block

                    // Monitor the block-breaking progress
                    while (client.interactionManager.updateBlockBreakingProgress(pos, Direction.DOWN)) {
                        Thread.sleep(50); // Simulate ticks (20 updates per second)
                    }

                    // Block breaking finished, reset flag and call callback with success
                    isBreaking.set(false);
                    System.out.println("Block broken!");
                    callback.onComplete(true);
                } else {
                    // Failed to start breaking the block
                    System.out.println("Failed to start breaking block.");
                    callback.onComplete(false);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                // Reset flag in case of error and call callback with failure
                isBreaking.set(false);
                callback.onComplete(false);
            }
        }).start();
    }
}
