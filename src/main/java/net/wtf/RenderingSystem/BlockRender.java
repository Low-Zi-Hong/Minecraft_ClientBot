package net.wtf.RenderingSystem;

import net.ClientInitialiser;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.systems.RenderSystem;
import net.wtf.AutoRun.Wocao;

public class BlockRender {

    public static void register(WorldRenderContext context) {
        // Register the rendering event
            if(!ClientInitialiser.blocKPosToRender.isEmpty()) {
                renderBlockOutline(context.matrixStack(), context.camera().getPos(), context.consumers());
            }
    }

    private static void renderBlockOutline(MatrixStack matrices, Vec3d cameraPos, VertexConsumerProvider vertexConsumers) {
        // Prepare rendering without depth test
        RenderSystem.enableBlend(); // Enable blending for transparency
        RenderSystem.defaultBlendFunc(); // Use default blending
        RenderSystem.disableDepthTest(); // Disable depth test to render through walls

        // Loop through each block in the path and render an outline
        for (Wocao.PathUnit blockPos : ClientInitialiser.blocKPosToRender) {
            // Calculate the block's position relative to the camera
            Box box = new Box(blockPos.targetPos).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            // Create a vertex consumer for lines (outline)
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

            // Render the outline
            WorldRenderer.drawBox(matrices, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ,
                    1.0F, 0.0F, 0.0F, 1.0F); // Red color (RGBA)
        }

        // Re-enable depth testing after rendering
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

}
