package com.gladurbad.nova.data.tracker;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.handler.EventProcessor;
import com.gladurbad.nova.data.tracker.handler.PacketProcessor;
import com.gladurbad.nova.data.tracker.handler.PostPacketProcessor;
import com.gladurbad.nova.data.tracker.impl.*;
import com.gladurbad.nova.network.wrapper.WrappedPacket;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.bukkit.event.Event;

import java.util.Collection;

public class TrackerManager {

    private final ClassToInstanceMap<Tracker> trackerMap;

    public TrackerManager(PlayerData data) {
        /*
         * Hey guys, welcome to the tracker manager.
         * You may have noticed by now, but I really love streams (not really please save me)
         *
         * "But glad!!!" you say, "there are so many streams here that it will severely impact performance" Bitch, how bout
         * my fist severely impact your face.
         */
        this.trackerMap = new ImmutableClassToInstanceMap.Builder<Tracker>()
                .put(ActionTracker.class, new ActionTracker(data))
                .put(AttributeTracker.class, new AttributeTracker(data))
                .put(ClickTracker.class, new ClickTracker(data))
                .put(CollisionTracker.class, new CollisionTracker(data))
                .put(EntityTracker.class, new EntityTracker(data))
                .put(MouseTracker.class, new MouseTracker(data))
                .put(PingTracker.class, new PingTracker(data))
                .put(PositionTracker.class, new PositionTracker(data))
                .put(VelocityTracker.class, new VelocityTracker(data))
                .build();
    }

    public void handlePacket(WrappedPacket packet) {
        Collection<Tracker> trackers = trackerMap.values();

        // Handle pre packet.
        trackers.stream()
                .filter(PacketProcessor.class::isInstance)
                .map(PacketProcessor.class::cast)
                .forEach(handler -> handler.process(packet));
    }

    public void handlePostPacket(WrappedPacket packet) {
        Collection<Tracker> trackers = trackerMap.values();

        // Handle pre packet.
        trackers.stream()
                .filter(PostPacketProcessor.class::isInstance)
                .map(PostPacketProcessor.class::cast)
                .forEach(handler -> handler.postProcess(packet));
    }

    public void handleEvent(Event event) {
        Collection<Tracker> trackers = trackerMap.values();

        // Handle pre packet.
        trackers.stream()
                .filter(EventProcessor.class::isInstance)
                .map(EventProcessor.class::cast)
                .forEach(handler -> handler.process(event));
    }

    public <T extends Tracker> T getTracker(Class<T> klass) {
        return trackerMap.getInstance(klass);
    }
}
