package com.gladurbad.nova.check.impl.timer;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.util.buffer.Buffer;
import com.gladurbad.nova.util.math.MathUtil;
import com.google.common.collect.Lists;

import java.util.Deque;

public class TimerB extends Check implements PacketHandler {

    public TimerB(PlayerData data) {
        super(data, "Timer (B)");
    }

    private final Deque<Long> samples = Lists.newLinkedList();
    private final Buffer buffer = new Buffer(8);
    private Long lastTimestamp;

    @Override
    public void handle(WrappedPacket packet) {
        if (packet instanceof CPacketFlying) {
            // Get the current packet timestamp.
            long timestamp = packet.getTime();

            // Make sure the last timestamp isn't null, or the delta calculation will be wrong.
            if (lastTimestamp != null) {
                // Add the delta time to the samples.
                samples.add(timestamp - lastTimestamp);

                /*
                 * This is a bad way of doing a timer check. It works, yet it's much more prone to client connection lag,
                 * and thread lag. The only advantage it has over the balance check is that it doesn't have any problems
                 * with balance abuse, where the client intentionally lowers their clock speed to reduce the balance into
                 * the negatives, essentially making a buffer where you can then use fast timer speed and bypass.
                 */
                if (samples.size() >= 20) {
                    // Calculate the average.
                    if (MathUtil.mean(samples) < 47.0) {
                        // Use a buffer since this check isn't really accurate.
                        if (buffer.add() > 3) fail();
                    } else {
                        // Reduce the buffer if the average was normal.
                        buffer.reduce(0.25);
                    }

                    // Clear the sample.
                    samples.clear();
                }
            }

            // Set the last timestamp.
            lastTimestamp = timestamp;
        }
    }
}
