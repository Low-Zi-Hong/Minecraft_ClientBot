package net.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MovementMixin {
    @Inject(method = "tickMovement", at = @At("HEAD"))

private void onTick(CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    ClientPlayerEntity player = client.player;

    // Custom movement logic: move the player in the X and Z direction
    if (player != null) {
        double currentX = player.getX();
        double currentY = player.getY();
        double currentZ = player.getZ();
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();

        // Move by 1 block in both X and Z directions
        double newX = currentX + 0.1;
        double newZ = currentZ + 0.1;

        // Send movement packet to update server
       // client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(newX, currentY, newZ, currentYaw, currentPitch, player.isOnGround()));

        // Optionally, send a message to the player to indicate movement
        //player.sendMessage(Text.literal("Moved using Mixin"));
    }
}
}
