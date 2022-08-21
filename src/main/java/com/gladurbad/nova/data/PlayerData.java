package com.gladurbad.nova.data;

import com.gladurbad.nova.check.Check;
import com.gladurbad.nova.check.CheckManager;
import com.gladurbad.nova.check.handler.PacketHandler;
import com.gladurbad.nova.data.tracker.Tracker;
import com.gladurbad.nova.data.tracker.TrackerManager;
import com.gladurbad.nova.network.manager.PacketManager;
import com.gladurbad.nova.network.wrapper.inbound.CPacketFlying;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerData {

    private final Player player;
    private final CheckManager checkManager;
    private final PacketManager packetManager;
    private final TrackerManager trackerManager;

    private int tick;

    public PlayerData(Player player) {
        this.player = player;

        this.packetManager = new PacketManager(this);
        this.trackerManager = new TrackerManager(this);
        this.checkManager = new CheckManager(this);

        // Start the packet manager.
        packetManager.start();

        // Add a listener to the packet handler.
        packetManager.addListener(packet -> {
            // Increment the ticks existed when the client moves.
            if (packet instanceof CPacketFlying) ++tick;

            // Handle the tracker processing for this packet.
            trackerManager.handlePacket(packet);

            // Run the packet checks next.
            for (PacketHandler check : checkManager.getPacketChecks()) check.handle(packet);

            // Handle the tracker post-processing.
            trackerManager.handlePostPacket(packet);
        });
    }

    public <T extends Tracker> T getTracker(Class<T> klass) {
        return trackerManager.getTrackerMap().getInstance(klass);
    }
}
