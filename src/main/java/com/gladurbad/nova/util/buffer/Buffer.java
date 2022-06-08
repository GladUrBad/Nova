package com.gladurbad.nova.util.buffer;

import lombok.Getter;

@Getter
public class Buffer {
    private final double limit;
    private double value;

    public Buffer(double limit) {
        this.limit = limit;
    }

    public Buffer() {
        this.limit = Double.MAX_VALUE;
    }

    public double add(double amount) {
        return value = Math.min(limit, value + amount);
    }

    public void reduce(double amount) {
        value = Math.max(0, value - amount);
    }

    public double add() {
        return add(1);
    }

    public void reduce() {
        reduce(1);
    }

    public void reset() {
        value = 0;
    }
}
