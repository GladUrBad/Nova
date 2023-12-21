package com.gladurbad.nova.check.impl.aim;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.check.handler.RotationHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketUseEntity;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.collision.BoundingBox;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.gladurbad.nova.util.math.MathUtil;
import com.gladurbad.nova.util.reach.ReachEntity;
import com.google.common.collect.Lists;
import net.minecraft.server.v1_8_R3.MathHelper;

import java.util.Deque;

public class AimC extends Check implements RotationHandler, PacketHandler {

    public AimC(PlayerData data) {
        super(data, "Aim (C)");
    }

    private final Deque<Float> offsets = Lists.newLinkedList(), deltas = Lists.newLinkedList();
    private final Buffer buffer = new Buffer(8);
    private ReachEntity target;

    @Override
    public void handle(WrappedPacket packet) {
        if (packet instanceof CPacketUseEntity) {
            // Get the tracked entity from the entity tracker.
            CPacketUseEntity wrapper = (CPacketUseEntity) packet;
            target = entityTracker.get(wrapper.getEntityId());
        }
    }

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Make sure the target isn't null first.
        if (target != null) {
            // Get the rotation yaw value wrapped to 180 degrees.
            float yaw = MathHelper.g(to.getYaw());

            // Get the target's bounding box.
            BoundingBox boundingBox = target.getBox().copy();

            /*
             * Since this anti-cheat base has a "perfect" entity tracker (apart from the scaling issues)
             * we can actually run a rotation generating method from an aim-bot cheat and compare that to the
             * actual rotations to get an idea if the player is using an aim-bot.
             *
             * Of course, not all aim-bots generate rotations the same way, however they are pretty similar
             * most of the time. I am only comparing yaw rotations and not pitch rotations, because they tend to vary
             * less in how those are calculated.
             *
             * We compare the wrapped rotation values since the generated one will be super offset from the client yaw
             * unless we wrap the two to 180 degrees.
             */
            double distanceX = boundingBox.posX() - from.getX();
            double distanceZ = boundingBox.posZ() - from.getZ();

            // Generate the yaw based on the distance using simple trig math. Wrap to 180F.
            float generatedYaw =  MathHelper.g((float) (Math.toDegrees(Math.atan2(distanceZ, distanceX)) - 90F));

            // Add the offset from the actual yaw and generated yaw to the sample.
            offsets.add(Math.abs(yaw - generatedYaw));

            // Add the delta yaw to the sample.
            deltas.add(Math.abs(to.getYaw() - from.getYaw()));

            /*
             * Easy way of checking if both samples are full. In this case there are two sample lists,
             * so we are checking at 30 per sample.
             *
             * We get the average and deviation of both as a measure of central tendency and as a measure of variation.
             *
             * A low average for the offsets indicates that the generated yaw and yaw are usually very similar,
             * indicating the player may be using an aim-bot.
             *
             * A low average for the deltas indicates the player did not move their mouse very much and should be
             * exempted to prevent false positives.
             *
             * A low deviation for the offsets indicates that the player has a consistent offset, meaning that while
             * the calculated offset might be high, the player is consistently offset, indicating they might be using an
             * aim-bot of a different rotation generation method.
             *
             * A low deviation for the deltas indicates that the player was not challenged enough during combat and
             * could hit the target at a consistent rotation, indicating that the target may be standing still. For
             * obvious reasons we would want to exempt this.
             */
            if (offsets.size() + deltas.size() == 60) {
                double averageDelta = MathUtil.mean(deltas);
                double averageOffset = MathUtil.mean(offsets);

                double deltasDeviation = MathUtil.deviation(deltas);
                double offsetsDeviation = MathUtil.deviation(offsets);

                //debug(String.format("ad=%.2f ao=%.2f dd=%.2f od=%.2f", averageDelta, averageOffset, deltasDeviation, offsetsDeviation));
                if (averageDelta > 2.5 && averageOffset < 3.0 && deltasDeviation > 1.0 && offsetsDeviation < 3.0) {
                    // Use a buffer since this check is based simply on improbability.
                    if (buffer.add() > 3) fail();
                } else {
                    buffer.reduce(0.25);
                }

                // Clear the sample.
                offsets.clear();
                deltas.clear();
            }
        }
    }
}
