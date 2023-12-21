package com.gladurbad.nova.util.collision;

import lombok.Getter;

@Getter
public class BoundingBox {
    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;

    public final long timestamp = System.currentTimeMillis();

    public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
        this.maxZ = Math.max(maxZ, minZ);
    }

    public BoundingBox(double x, double y, double z, float width, float height) {
        this.minX = x - (width / 2.0F);
        this.minY = y;
        this.minZ = z - (width / 2.0F);
        this.maxX = x + (width / 2.0F);
        this.maxY = y + height;
        this.maxZ = z + (width / 2.0F);
    }

    public BoundingBox expand(double x, double y, double z) {
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public boolean collides(BoundingBox other) {
        return other.maxX >= this.minX
                && other.minX <= this.maxX
                && other.maxY >= this.minY
                && other.minY <= this.maxY
                && other.maxZ >= this.minZ
                && other.minZ <= this.maxZ;
    }

    public boolean collidesUnder(BoundingBox other) {
        return maxY == other.minY
                && minZ < other.maxZ
                && minX < other.maxX
                && maxZ > other.minZ
                && maxX > other.minX;
    }

    public boolean collidesAbove(BoundingBox other) {
        return minY == other.maxY
                && minZ < other.maxZ
                && minX < other.maxX
                && maxZ > other.minZ
                && maxX > other.minX;
    }

    public boolean collidesHorizontally(BoundingBox other) {
        boolean vertical = maxY > other.minY && minY < other.maxY;
        boolean horizontal = minX == other.maxX || maxX == other.minX || minZ == other.maxZ || maxZ == other.minZ;

        return vertical && horizontal;
    }

    public BoundingBox copy() {
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public double posX() {
        return (maxX + minX) / 2.0;
    }

    public double posY() {
        return minY;
    }

    public double posZ() {
        return (maxZ + minZ) / 2.0;
    }
}