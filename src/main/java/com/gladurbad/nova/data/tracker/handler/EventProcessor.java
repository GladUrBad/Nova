package com.gladurbad.nova.data.tracker.handler;

import org.bukkit.event.Event;

public interface EventProcessor {
    // Processes a bukkit event for the tracker.
    void process(Event event);
}
