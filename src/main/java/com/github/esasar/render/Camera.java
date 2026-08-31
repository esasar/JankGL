package com.github.esasar.render;

import com.github.esasar.math.Vec3;

/** Camera. */
public record Camera(Vec3 position, double yaw, double pitch) {

    public static Camera of(Vec3 position, double yaw, double pitch) { return new Camera(position, yaw, pitch); }

    public Vec3 toView(Vec3 p) {
        return p.minus(position()).rotateXZ(-yaw()).rotateYZ(-pitch());
    }

    public Vec3 forward() {
        return Vec3.of(-Math.sin(yaw()), 0, Math.cos(yaw()));
    }

    public Vec3 right() {
        return Vec3.of(Math.cos(yaw()), 0, Math.sin(yaw()));
    }
}