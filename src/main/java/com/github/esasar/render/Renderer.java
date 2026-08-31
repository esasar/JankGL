package com.github.esasar.render;

import com.github.esasar.math.Vec2;
import com.github.esasar.math.Vec3;
import com.github.esasar.scene.Instance;
import com.github.esasar.scene.Scene;

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

        // draw lines between edges
        for (var e : instance.mesh().edges()) {
            var segMaybe = Clip.near(view[e.a()], view[e.b()], NEAR);
            if (segMaybe.isEmpty()) continue;

            var pa = project(segMaybe.get().a());
            var pb = project(segMaybe.get().b());

            line(sx(pa), sy(pa), sx(pb), sy(pb), instance.color());
        }
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
