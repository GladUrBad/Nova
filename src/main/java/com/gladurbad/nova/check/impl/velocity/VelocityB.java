package com.gladurbad.nova.check.impl.velocity;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketUseEntity;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VelocityB extends Check implements PositionHandler, PacketHandler {

    public VelocityB(PlayerData data) {
        super(data, "Velocity (B)");
    }

    private final Buffer buffer = new Buffer(5);
    private Float friction;
    private int lastSlowdown;

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        double horizontalVelocity = velocityTracker.getHorizontalVelocity();

        if (horizontalVelocity > 0 && friction != null) {
            /*
             * Essentially with this velocity check we're not aiming to be super precise, just
             * make a check that gets the blatant stuff well enough.
             *
             * First we create the reduction which is just the friction.
             * We then make a rudimentary threshold which takes in multiple factors and reduces the threshold if needed.
             * There is a lot more to factor in, and a lot of this isn't really correct. It's just a basic threshold
             * to flag blatant velocity.
             */
            float reduction = from.isOnGround() ? friction * 0.91F : 0.91F;

            double threshold = horizontalVelocity
                    * reduction
                    * (data.getTick() - lastSlowdown <= 1 ? 0.4 : 1.0);

            // Get the horizontal distance.
            double distance = to.horizontalDistance(from);

            if (distance < threshold) {
                if (buffer.add() > 3) fail();
            } else {
                // This check is so lenient even though it's wrong I don't have to reduce buffer that much.
                buffer.reduce(0.1);
            }
        }

        // Set the last friction value.
        friction = MinecraftServer.getServer()
                .getWorld()
                .getType(new BlockPosition(to.getX(), to.getY() - 1.0, to.getZ()))
                .getBlock().frictionFactor;
    }

    @Override
    public void handle(WrappedPacket packet) {
        if (packet instanceof CPacketUseEntity) {
            CPacketUseEntity wrapper = (CPacketUseEntity) packet;

            CraftWorld craftWorld = (CraftWorld) data.getPlayer().getWorld();
            net.minecraft.server.v1_8_R3.World nmsWorld = craftWorld.getHandle();
            net.minecraft.server.v1_8_R3.Entity nmsEntity = nmsWorld.a(wrapper.getEntityId());
            Entity entity = nmsEntity.getBukkitEntity();

            if (entity instanceof Player && actionTracker.isSprinting()) {
                lastSlowdown = data.getTick();
            }
        }
    }
}
