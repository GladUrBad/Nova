package com.gladurbad.nova.check.impl.timer;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import com.gladurbad.nova.network.wrapper.outbound.SPacketPosition;
import com.gladurbad.nova.util.buffer.Buffer;

public class TimerA extends Check implements PacketHandler {

    public TimerA(PlayerData data) {
        super(data, "Timer (A)");
    }

    private final Buffer buffer = new Buffer(5);
    private Long lastTimestamp;
    private long balance = -50L;

    @Override
    public void handle(WrappedPacket packet) {
        if (packet instanceof CPacketFlying) {
            /*
             * Since we are injecting the packet handler on PlayerJoinEvent that means a lot of flying packets
             * could be queued for processing before the player actually joins the server. Exempt this. This exempt
             * is definitely not perfect, it's just for the concept.
             */
            if (data.getTick() < 100) return;

            // Get the time the packet was sent.
            long timestamp = packet.getTime();

            // Make sure the last timestamp was not null else this will false.
            if (lastTimestamp != null) {
                /*
                 * This timer check is called a balance, or allowance check. Simply put, we are
                 * simulating the client's clock serverside and comparing it to the time deltas clientside.
                 * These measurements are not perfect because of thread lag. This is especially true since our packet
                 * system processes on the main thread, and since there is a lot of processing on ONE server thread,
                 * rather than less processing on multiple netty threads, that means this check is more prone to false
                 * positive due to thread lag being more probable. Did I exempt this? No, but you should definitely
                 * account for this if you want to use this check in production.
                 *
                 * This check is essentially lag-proof to client connection lag, since if the connection freezes, yes
                 * there will be a bunch of zeroes subtracted from the balance, but the first flying sent after the
                 * connection resumes will always have a delta time of however long that connection froze for.
                 *
                 * Still, I'm using a buffer since this check is not perfect and the client clock isn't either, since
                 * you can actually make the game send less movement packets than normal.
                 */
                balance += 50L;
                balance -= timestamp - lastTimestamp;

                // Player clock-speed was offset by over 1 tick.
                if (balance > 50L) {
                    if (buffer.add() > 1) fail();

                    // Reset the balance.
                    balance = -50L;
                } else {
                    // Reduce the buffer by 1.0 every 1200 movements or 60 seconds.
                    buffer.reduce(0.0008);
                }
            }

            // Set the last timestamp.
            lastTimestamp = timestamp;
        } else if (packet instanceof SPacketPosition) {
            // When the player teleports, the client sends an extra movement packet. 
            balance -= 50L;
        }
    }
}
