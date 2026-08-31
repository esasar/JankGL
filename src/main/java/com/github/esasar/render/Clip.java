package com.github.esasar.render;

import com.github.esasar.math.Vec3;

import java.util.Optional;

public final class Clip {
    /** Line segment between two points. */
    public record Segment(Vec3 a, Vec3 b) {
        public static Segment of(Vec3 a, Vec3 b) { return new Segment(a, b); }
    }

    private Clip() {}

    /** Calculates clipping for a line segment between a and b. */
    public static Optional<Segment> near(Vec3 a, Vec3 b, double near) {
        var aIn = a.z() >= near;
        var bIn = b.z() >= near;

        if (aIn && bIn) return Optional.of(Segment.of(a, b));
        if (!aIn && !bIn) return Optional.empty();

        var t = (near - a.z()) / (b.z() - a.z());
        var hit = a.plus(b.minus(a).scale(t));

        return aIn ? Optional.of(Segment.of(a, hit)) : Optional.of(Segment.of(hit, b));
    }
}
