package com.gladurbad.nova.check.impl.speed;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.math.MathUtil;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;

public class SpeedA extends Check implements PositionHandler {

    public SpeedA(PlayerData data) {
        super(data, "Speed (A)");
    }

    private final Buffer buffer = new Buffer(8);
    private Double lastDistance;
    private Float friction;
    private Boolean lastOffset;

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the horizontal distance.
        double distance = to.horizontalDistance(from);

        // Make sure the last values aren't null since we need them for the check.
        if (lastOffset != null && friction != null) {
            // Get the attribute of the player.
            double attribute = attributeTracker.getWalkSpeed();
            attribute *= 1.3F;
            attribute *= 1.0F + (attributeTracker.getSpeedModifier() * 0.2F);

            // Calculate the max speed distance based on Minecraft protocol.
            if (from.isOnGround()) {
                friction *= 0.91F;
                attribute *= 0.16277136F / Math.pow(friction, 3.0);

                if (!to.isOnGround() && to.getY() > from.getY()) attribute += 0.2F;
            } else {
                attribute = 0.026F;
                friction = 0.91F;
            }

            // Add the horizontal velocity to the attribute.
            attribute += MathUtil.hypot(velocityTracker.getX(), velocityTracker.getZ());

            // These aren't protocol but I added them to make the check more stable.
            if (lastOffset) attribute += 0.05;
            if (collisionTracker.isWeb()) attribute += 0.01;

            if (lastDistance != null) {
                // Represents any excess distance, anything above 0.001 is probably cheating.
                double excess = distance - lastDistance - attribute;

                // Exempt for edge cases.
                boolean exempt = attributeTracker.isFlying()
                        || positionTracker.isTeleporting()
                        || collisionTracker.isLiquid();

                if (excess > 0.001 && !exempt) {
                    if (buffer.add() > 3) fail();
                } else {
                    buffer.reduce(0.05);
                }
            }

            // Set the last distance as the current times the friction.
            lastDistance = distance * friction;
        }

        // Set the last position offset status.
        lastOffset = positionTracker.isOffsetPosition();

        // Set the last friction value.
        friction = MinecraftServer.getServer()
                .getWorld()
                .getType(new BlockPosition(to.getX(), to.getY() - 1.0, to.getZ()))
                .getBlock().frictionFactor;
    }
}
