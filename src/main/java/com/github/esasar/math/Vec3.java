package com.github.esasar.math;

/** 3D vector. */
public record Vec3(double x, double y, double z) {
    public static final Vec3 ORIGO = Vec3.of(0, 0, 0);

    public static Vec3 of(double x, double y, double z) { return new Vec3(x, y, z); }

    public Vec3 translateX(double dx) { return Vec3.of(x() + dx, y(), z()); }
    public Vec3 translateY(double dy) { return Vec3.of(x(), y() + dy, z()); }
    public Vec3 translateZ(double dz) { return Vec3.of(x(), y(), z() + dz); }

    public Vec3 scale(double s) { return Vec3.of(s * x(), s * y(), s * z()); }

    public Vec3 minus(Vec3 p) { return Vec3.of(x() - p.x(), y() - p.y(), z() - p.z()); }
    public Vec3 minus(Vec3 p, double scale) { return Vec3.of(x() - scale * p.x(), y() - scale * p.y(), z() - scale * p.z()); }

    public Vec3 plus(Vec3 p) { return Vec3.of(x() + p.x(), y() + p.y(), z() + p.z()); }
    public Vec3 plus(Vec3 p, double scale) { return Vec3.of(x() + scale * p.x(), y() + scale * p.y(), z() + scale * p.z()); }

    public Vec3 rotateXZ(double angle) {
        var c = Math.cos(angle);
        var s = Math.sin(angle);
        return Vec3.of(x() * c - z() * s, y(), x() * s + z() * c);
    }

    public Vec3 rotateYZ(double angle) {
        var c = Math.cos(angle);
        var s = Math.sin(angle);
        return Vec3.of(x(), y() * c - z() * s, y() * s + z() * c);
    }

    public Vec3 rotateXY(double angle) {
        var c = Math.cos(angle);
        var s = Math.sin(angle);
        return Vec3.of(x() * c + y() * s, y() * c - x() * s, z());
    }
}