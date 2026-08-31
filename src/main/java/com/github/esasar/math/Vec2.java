package com.github.esasar.math;

/** 2D vector. */
public record Vec2(double x, double y) {
    public static Vec2 of(double x, double y) { return new Vec2(x, y); }
}
