package com.github.esasar.render;

import com.github.esasar.math.Vec2;
import com.github.esasar.math.Vec3;
import com.github.esasar.scene.Instance;
import com.github.esasar.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public final class Renderer {
    /** Threshold for clip lines drawn by {@link #line}. */
    private static final double NEAR = 0.05;

    private final FrameBuffer fb;
    private final double aspect;
    private final double focal;

    public FrameBuffer getFb() { return fb; }
    public double getAspect() { return aspect; }
    public double getFocal() { return focal; }

    public Renderer(FrameBuffer fb, double fovRad) {
        this.fb = fb;
        this.aspect = (double) fb.getWidth() / fb.getHeight();
        this.focal = 1d / Math.tan(fovRad / 2d);
    }

    /** Render all instances in the scene from the pov of the camera. */
    public void render(Scene scene, Camera camera) {
        getFb().clear(Color.BLACK.getValue());
        scene.instances().forEach(i -> draw(i, camera));
    }

    /** Draw instance from the pov of camera. */
    private void draw(Instance instance, Camera camera) {
        // convert vertices to view coordinates
        var local = instance.mesh().vertices();
        var view = new Vec3[local.size()];
        for (var i = 0; i < view.length; i++) {
            view[i] = camera.toView(instance.toWorld(local.get(i)));
        }

        // fill faces
        for (var f : instance.mesh().faces()) {
            fillFace(view[f.a()], view[f.b()], view[f.c()], instance.color());
        }

        // draw lines between edges
        for (var e : instance.mesh().edges()) {
            var segMaybe = Clip.near(view[e.a()], view[e.b()], NEAR);
            if (segMaybe.isEmpty()) continue;

            var pa = project(segMaybe.get().a());
            var pb = project(segMaybe.get().b());

            line(sx(pa), sy(pa), sx(pb), sy(pb), instance.color());
        }
    }

    /** Clip a triangle against the near plane and rasterize the resulting polygon. */
    private void fillFace(Vec3 a, Vec3 b, Vec3 c, int color) {
        var poly = clipNear(List.of(a, b, c));
        if (poly.size() < 3) return;

        var p0 = project(poly.getFirst());
        for (var i = 1; i < poly.size() - 1; i++) {
            var p1 = project(poly.get(i));
            var p2 = project(poly.get(i + 1));
            triangle(sx(p0), sy(p0), sx(p1), sy(p1), sx(p2), sy(p2), color);
        }
    }

    /** <a href="https://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm">Sutherland-Hodgman</a>. */
    private List<Vec3> clipNear(List<Vec3> poly) {
        var out = new ArrayList<Vec3>();
        for (var i = 0; i < poly.size(); i++) {
            var cur = poly.get(i);
            var prev = poly.get((i - 1 + poly.size()) % poly.size());
            var curIn = cur.z() >= NEAR;
            var prevIn = prev.z() >= NEAR;

            if (curIn != prevIn) {
                var t = (NEAR - prev.z()) / (cur.z() - prev.z());
                out.add(prev.plus(cur.minus(prev).scale(t)));
            }
            if (curIn) out.add(cur);
        }
        return out;
    }

    /** Fills a screen-space triangle using edge functions. */
    private void triangle(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        // find the smallest rectangle that fits the triangle
        var minX = Math.max(0, Math.min(x0, Math.min(x1, x2)));
        var maxX = Math.min(getFb().getWidth() - 1, Math.max(x0, Math.max(x1, x2)));
        var minY = Math.max(0, Math.min(y0, Math.min(y1, y2)));
        var maxY = Math.min(getFb().getHeight() - 1, Math.max(y0, Math.max(y1, y2)));

        // walk through the rectangle
        for (var y = minY; y <= maxY; y++) {
            for (var x = minX; x <= maxX; x++) {
                // cross-products
                var w0 = edge(x1, y1, x2, y2, x, y);
                var w1 = edge(x2, y2, x0, y0, x, y);
                var w2 = edge(x0, y0, x1, y1, x, y);

                // when cross-products signs match, the pixel is inside the triangle
                if ((w0 >= 0 && w1 >= 0 && w2 >= 0) || (w0 <= 0 && w1 <= 0 && w2 <= 0)) {
                    getFb().set(x, y, color);
                }
            }
        }
    }

    /** Cross product. TODO: maybe instead use objects or something */
    private int edge(int ax, int ay, int bx, int by, int cx, int cy) {
        return (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
    }

    /** Projects a world point to view coordinates. */
    private Vec2 project(Vec3 p) {
        return Vec2.of(getFocal() * p.x() / (p.z() * getAspect()),
                       getFocal() * p.y() / p.z());
    }

    /** Maps a projected point's x in [-1, 1] to a pixel column. */
    private int sx(Vec2 p) {
        return (int) (getFb().getWidth() * (p.x() + 1) / 2);
    }

    /** Maps a projected point's y in [-1, 1] to a pixel row. */
    public int sy(Vec2 p) {
        return (int) (getFb().getHeight() * (1 - p.y()) / 2);
    }

    /** <a href="https://www.cs.helsinki.fi/group/goa/mallinnus/lines/bresenh.html">Bresenham</a> */
    private void line(int x0, int y0, int x1, int y1, int color) {
        var dx = Math.abs(x1 - x0);
        var dy = -Math.abs(y1 - y0);
        var sx = x0 < x1 ? 1 : -1;
        var sy = y0 < y1 ? 1 : -1;
        var err = dx + dy;

        while (true) {
            getFb().set(x0, y0, color);
            if (x0 == x1 && y0 == y1) break;
            var e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
}
