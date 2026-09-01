package com.github.esasar.render;

import com.github.esasar.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Clip {
    /** Line segment between two points. */
    public record Segment(Vec3d a, Vec3d b) {
        public static Segment of(Vec3d a, Vec3d b) { return new Segment(a, b); }
    }

    private Clip() {}

    /** Calculates clipping for a line segment between a and b. */
    public static Optional<Segment> near(Vec3d a, Vec3d b, double near) {
        var aIn = a.z() >= near;
        var bIn = b.z() >= near;

        if (aIn && bIn) return Optional.of(Segment.of(a, b));
        if (!aIn && !bIn) return Optional.empty();

        var t = (near - a.z()) / (b.z() - a.z());
        var hit = a.plus(b.minus(a).scale(t));

        return aIn ? Optional.of(Segment.of(a, hit)) : Optional.of(Segment.of(hit, b));
    }

    /** <a href="https://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm">Sutherland-Hodgman</a>. */
    public static List<Vec3d> clipNear(List<Vec3d> poly, double near) {
        var out = new ArrayList<Vec3d>();
        for (var i = 0; i < poly.size(); i++) {
            var cur = poly.get(i);
            var prev = poly.get((i - 1 + poly.size()) % poly.size());
            var curIn = cur.z() >= near;
            var prevIn = prev.z() >= near;

            if (curIn != prevIn) {
                var t = (near - prev.z()) / (cur.z() - prev.z());
                out.add(prev.plus(cur.minus(prev).scale(t)));
            }
            if (curIn) out.add(cur);
        }
        return out;
    }
}
