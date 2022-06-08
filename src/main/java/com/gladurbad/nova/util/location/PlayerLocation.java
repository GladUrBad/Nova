package com.gladurbad.nova.util.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerLocation {
    private long timestamp;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public PlayerLocation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.timestamp = System.currentTimeMillis();
    }

    public PlayerLocation(double x, double y, double z, float yaw, float pitch, boolean onGround, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.timestamp = timestamp;
    }

    public double distanceSquared(PlayerLocation other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;

        return dx * dx + dy * dy + dz * dz;
    }

    public double horizontalDistance(PlayerLocation other) {
        double dx = x - other.x;
        double dz = z - other.z;

        return Math.sqrt(dx * dx + dz * dz);
    }

    public PlayerLocation copy() {
        return new PlayerLocation(x, y, z, yaw, pitch, onGround, timestamp);
    }
}