package com.github.esasar.math;

/** 3D vector for doubles. */
public record Vec3d(double x, double y, double z) {
    public static final Vec3d ORIGO = Vec3d.of(0, 0, 0);

    public static Vec3d of(double x, double y, double z) { return new Vec3d(x, y, z); }

    public Vec3d translateX(double dx) { return Vec3d.of(x() + dx, y(), z()); }
    public Vec3d translateY(double dy) { return Vec3d.of(x(), y() + dy, z()); }
    public Vec3d translateZ(double dz) { return Vec3d.of(x(), y(), z() + dz); }

    public Vec3d scale(double s) { return Vec3d.of(s * x(), s * y(), s * z()); }

    public Vec3d minus(Vec3d p) { return Vec3d.of(x() - p.x(), y() - p.y(), z() - p.z()); }
    public Vec3d minus(Vec3d p, double scale) { return Vec3d.of(x() - scale * p.x(), y() - scale * p.y(), z() - scale * p.z()); }

    public Vec3d plus(Vec3d p) { return Vec3d.of(x() + p.x(), y() + p.y(), z() + p.z()); }
    public Vec3d plus(Vec3d p, double scale) { return Vec3d.of(x() + scale * p.x(), y() + scale * p.y(), z() + scale * p.z()); }

    public Vec3d cross(Vec3d v) {
        return Vec3d.of(y() * v.z() - z() * v.y(),
                        z() * v.x() - x() * v.z(),
                        x() * v.y() - y() * v.x());
    }

    public double dot(Vec3d v) {
        return x() * v.x() + y() * v.y() + z() * v.z();
    }

    public Vec3d normalize() {
        var len = Math.sqrt(dot(this));
        return len == 0 ? this : scale(1 / len);
    }

    public Vec3d rotateXZ(double angle) {
        var c = Math.cos(angle);
        var s = Math.sin(angle);
        return Vec3d.of(x() * c - z() * s, y(), x() * s + z() * c);
    }

    public Vec3d rotateYZ(double angle) {
        var c = Math.cos(angle);
        var s = Math.sin(angle);
        return Vec3d.of(x(), y() * c - z() * s, y() * s + z() * c);
    }

    public Vec3d rotateXY(double angle) {
        var c = Math.cos(angle);
        var s = Math.sin(angle);
        return Vec3d.of(x() * c + y() * s, y() * c - x() * s, z());
    }

    @Override
    public String toString() {
        return String.format("(%.0f, %.0f, %.0f)", x(), y(), z());
    }
}