package com.gladurbad.nova.check;

import com.gladurbad.nova.data.PlayerData;
import com.gladurbad.nova.data.tracker.impl.*;
import com.gladurbad.nova.util.string.StringUtil;
import org.bukkit.Bukkit;

public class Check {

    private final String format;

    protected final PlayerData data;
    protected final ActionTracker actionTracker;
    protected final AttributeTracker attributeTracker;
    protected final ClickTracker clickTracker;
    protected final CollisionTracker collisionTracker;
    protected final EntityTracker entityTracker;
    protected final MouseTracker mouseTracker;
    protected final PingTracker pingTracker;
    protected final PositionTracker positionTracker;
    protected final VelocityTracker velocityTracker;

    private int violationLevel;

    public Check(PlayerData data, String name) {
        this.data = data;

        // Cache this, it might be the one thing about this anticheat that won't lag your server.
        this.format = StringUtil.color( "&7[&5N&7] &d%player% &7is using &d%check% &c(x%vl%)")
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%check%", name);

        // Don't want to have to do chained getters in every check so just store a reference of every tracker.
        this.actionTracker = data.getTracker(ActionTracker.class);
        this.attributeTracker = data.getTracker(AttributeTracker.class);
        this.clickTracker = data.getTracker(ClickTracker.class);
        this.collisionTracker = data.getTracker(CollisionTracker.class);
        this.entityTracker = data.getTracker(EntityTracker.class);
        this.mouseTracker = data.getTracker(MouseTracker.class);
        this.pingTracker = data.getTracker(PingTracker.class);
        this.positionTracker = data.getTracker(PositionTracker.class);
        this.velocityTracker = data.getTracker(VelocityTracker.class);
    }

    protected void debug(Object debug) {
        Bukkit.broadcastMessage(String.valueOf(debug));
    }

    protected void fail() {
        Bukkit.broadcastMessage(format.replaceAll("%vl%", String.valueOf(++violationLevel)));
    }
}
