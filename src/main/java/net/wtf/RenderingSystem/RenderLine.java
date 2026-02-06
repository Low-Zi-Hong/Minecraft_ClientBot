package net.wtf.RenderingSystem;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ClientInitialiser;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class RenderLine {

    public static void register(WorldRenderContext context) {
        // Register the rendering event
            if (!ClientInitialiser.blocKPosToRender.isEmpty()) {
                renderLineOutput(context.matrixStack(), context.camera().getPos(), context.consumers().getBuffer(RenderLayer.getLines()));
            }
    }

    private static void renderLineOutput(MatrixStack matrices, Vec3d pos, VertexConsumer vertexConsumer) {

        RenderSystem.enableBlend(); // Enable blending for transparency
        RenderSystem.defaultBlendFunc(); // Use default blending
        RenderSystem.disableDepthTest(); // Disable depth test to render through walls

        MatrixStack.Entry entry = matrices.peek();

        for (int i = 0; i < ClientInitialiser.blocKPosToRender.size() - 1; i++) {
            Vec3d startPos = ClientInitialiser.blocKPosToRender.get(i).targetPos.toCenterPos().subtract(pos.x, pos.y, pos.z);
            Vec3d endPos = ClientInitialiser.blocKPosToRender.get(i + 1).targetPos.toCenterPos().subtract(pos.x, pos.y, pos.z);

            vertexConsumer.vertex(entry, (float) startPos.getX(), (float) startPos.getY(), (float) startPos.getZ()).color(0.0f, 1.0f, 0.0f, 1.0f).normal(0.0f, 1.0f, 0.0f);
            vertexConsumer.vertex(entry, (float) endPos.getX(), (float) endPos.getY(), (float) endPos.getZ()).color(0.0f, 1.0f, 0.0f, 1.0f).normal(0.0f, 1.0f, 0.0f);
        }

        // Re-enable depth testing after rendering
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

}
