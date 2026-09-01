package com.github.esasar.render;

import com.github.esasar.math.Vec3d;

/** Camera. */
public record Camera(Vec3d position, double yaw, double pitch) {

    public static Camera of(Vec3d position, double yaw, double pitch) { return new Camera(position, yaw, pitch); }

    public Vec3d toView(Vec3d p) {
        return p.minus(position()).rotateXZ(-yaw()).rotateYZ(-pitch());
    }

    /** Rotates a direction (e.g. a light direction) into view space, without translating it. */
    public Vec3d toViewDir(Vec3d d) {
        return d.rotateXZ(-yaw()).rotateYZ(-pitch());
    }

    public Vec3d forward() {
        return Vec3d.of(-Math.sin(yaw()), 0, Math.cos(yaw()));
    }

    public Vec3d right() {
        return Vec3d.of(Math.cos(yaw()), 0, Math.sin(yaw()));
    }
}