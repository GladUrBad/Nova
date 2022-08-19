package com.gladurbad.nova.check.impl.velocity;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketUseEntity;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.math.MathUtil;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VelocityB extends Check implements PositionHandler, PacketHandler {

    public VelocityB(PlayerData data) {
        super(data, "Velocity (B)");
    }

    private final Buffer buffer = new Buffer(5);
    private boolean attacking;
    private Float friction;

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the current applied velocities confirmed by transactions.
        double velocityX = velocityTracker.getX();
        double velocityZ = velocityTracker.getZ();

        // Get the current movement deltas.
        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();

        // Make sure the player is taking any velocity before checking.
        boolean hasVelocity = velocityX != 0 || velocityZ != 0;

        if (hasVelocity && friction != null) {
            /*
             * This is where the simulation starts.
             * Basically we are doing a nested for loop which simulates client movement based on given inputs
             * which we cannot check via packets or other methods.
             *
             * For example, we cannot know which movement keys the player is pressing. We cannot know
             * exactly when the player is jumping. We can know when the player is sprinting, however I don't rely
             * on the status based on packets because of de-sync. I also simulate ground since there seems to be some
             * issues on slimes/stepping when I don't.
             *
             * We take those unknowns and run a movement simulation. We get the resulting deltas and compare to the
             * actual deltas. An offset is created, if the offset is smaller than what we currently have saved, we set
             * the offset to the result of that simulation.
             *
             * This is a very simple way to create accurate movement/velocity checks.
             *
             * I don't really want to go in-depth on the actual guts of the simulation since all of that is ripped
             * from the 1.8 client. There's not really much reason for me to explain it.
             */
            Double min = null;

            for (float strafe = -0.98F; strafe <= 0.98F; strafe += 0.98F) {
                for (float forward = -0.98F; forward <= 0.98F; forward += 0.98F) {
                    for (boolean jump : new boolean[]{true, false}) {
                        for (boolean sprint : new boolean[]{true, false}) {
                            for (boolean ground : new boolean[]{true, false}) {
                                double simulationX = velocityX;
                                double simulationZ = velocityZ;

                                if (Math.abs(simulationX) < 0.005D) simulationX = 0.0D;
                                if (Math.abs(simulationZ) < 0.005D) simulationZ = 0.0D;

                                if (attacking) {
                                    simulationX *= 0.6;
                                    simulationZ *= 0.6;
                                }

                                if (jump && sprint) {
                                    simulationX -= MathHelper.sin(to.getYaw() * 0.017453292F) * 0.2F;
                                    simulationZ += MathHelper.cos(to.getYaw() * 0.017453292F) * 0.2F;
                                }

                                float friction = ground ? this.friction * 0.91F : 0.91F;

                                float attribute = ground
                                        ? (float) (attributeTracker.getAttributeSpeed() * 0.16277136F / Math.pow(friction, 3.0))
                                        : sprint ? 0.026F : 0.02F;

                                double[] appliedVelocities = moveFlying(simulationX, simulationZ, strafe, forward, attribute, to.getYaw());

                                simulationX = appliedVelocities[0];
                                simulationZ = appliedVelocities[1];

                                double offsetX = deltaX - simulationX;
                                double offsetZ = deltaZ - simulationZ;

                                double offset = offsetX * offsetX + offsetZ * offsetZ;

                                if (min == null || offset < min) min = offset;
                            }
                        }
                    }
                }
            }

            // So this check isn't perfect, handling for collisions and other things still needs to be added.
            if (min > 0.001) {
                if (buffer.add() > 3) {
                    fail();
                }
            } else {
                buffer.reduce(0.1);
            }
        }

        // Set the last friction value.
        friction = MinecraftServer.getServer()
                .getWorld()
                .getType(new BlockPosition(to.getX(), to.getY() - 1.0, to.getZ()))
                .getBlock().frictionFactor;

        // Reset the attacking status.
        attacking = false;
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
                attacking = true;
            }
        }
    }

    private double[] moveFlying(double motionX, double motionZ, float strafe, float forward, float friction, float yaw) {
        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
            float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);
            motionX += (double) (strafe * f2 - forward * f1);
            motionZ += (double) (forward * f2 + strafe * f1);
        }

        return new double[]{motionX, motionZ};
    }
}
