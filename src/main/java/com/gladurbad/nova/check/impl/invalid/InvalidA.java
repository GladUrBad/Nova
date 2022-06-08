package com.gladurbad.nova.check.impl.invalid;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.check.handler.PositionHandler;
import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.gladurbad.nova.network.wrapper.outbound.SPacketPosition;
import com.gladurbad.nova.util.location.PlayerLocation;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Deque;

public class InvalidA extends Check implements PositionHandler, PacketHandler {

    public InvalidA(PlayerData data) {
        super(data, "Invalid (A)");
    }

    private final Deque<Teleport> teleports = Lists.newLinkedList();

    @Override
    public void handle(PlayerLocation to, PlayerLocation from) {
        // Get the current position values.
        double x = to.getX();
        double y = to.getY();
        double z = to.getZ();

        // Get the most recently sent teleport.
        Teleport latest = teleports.peek();

        // Remove the teleport if the position matches.
        if (latest != null && latest.getX() == x && latest.getY() == y && latest.getZ() == z) {
            teleports.poll();
        }

        /*
         * With this check we are ensuring the client does respond to teleport packets.
         * Essentially we are making a very lenient limit for the client to respond, however they do have to respond.
         * If they do not respond then they may have modified the client protocol to not respond to teleport packets.
         * This can be abused by forcing the server to send teleport packets and not responding, allowing for a timer
         * bypass or worse.
         */
        if (teleports.stream().filter(teleport -> data.getTick() - teleport.getTick() > 600).count() > 3) {
            data.getPlayer().kickPlayer("Lost teleports located.");
        }
    }

    @Override
    public void handle(WrappedPacket packet) {
        if (packet instanceof SPacketPosition) {
            // Add a new teleport to the list.
            SPacketPosition wrapper = (SPacketPosition) packet;
            teleports.add(new Teleport(data.getTick(), wrapper.getX(), wrapper.getY(), wrapper.getZ()));
        }
    }

    @AllArgsConstructor
    @Getter
    private static final class Teleport {
        private final int tick;
        private final double x, y, z;
    }
}
