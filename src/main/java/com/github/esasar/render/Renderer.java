package com.github.esasar.render;

import com.github.esasar.math.Vec2d;
import com.github.esasar.math.Vec3d;
import com.github.esasar.scene.Instance;
import com.github.esasar.scene.Scene;

import java.util.Arrays;
import java.util.List;

public final class Renderer {
    /** Threshold for clip lines drawn by {@link #line}. */
    private static final double NEAR = 0.05;

    /** Light every face receives regardless of its orientation towards the light. */
    private static final double AMBIENT = 0.2;

    private final FrameBuffer fb;
    private final double aspect;
    private final double focal;

    /** Depth buffer. */
    private final double[] depth;

    public FrameBuffer getFb() { return fb; }
    public double getAspect() { return aspect; }
    public double getFocal() { return focal; }

    public Renderer(FrameBuffer fb, double fovRad) {
        this.fb = fb;
        this.aspect = (double) fb.getWidth() / fb.getHeight();
        this.focal = 1d / Math.tan(fovRad / 2d);
        this.depth = new double[fb.getWidth() * fb.getHeight()];
    }

    /** Render all instances in the scene from the pov of the camera, lit by light. */
    public void render(Scene scene, Camera camera, DirectionalLight light, PointLight pointlight) {
        getFb().clear(Color.BLACK.getValue());
        Arrays.fill(depth, 0);
        var lightDir = camera.toViewDir(light.direction().normalize());
        scene.instances().forEach(i -> draw(i, camera, lightDir, light.intensity(), pointlight));
    }

    /** Draw instance from the pov of camera, lit by a light coming from lightDir (in view space). */
    private void draw(Instance instance, Camera camera, Vec3d lightDir, double lightIntensity, PointLight pointlight) {
        // convert vertices to view coordinates
        var local = instance.mesh().vertices();
        var view = new Vec3d[local.size()];
        for (var i = 0; i < view.length; i++) {
            view[i] = camera.toView(instance.toWorld(local.get(i)));
        }

        var spotlightPos = camera.toView(pointlight.position());

        // fill faces
        for (var f : instance.mesh().faces()) {
            var a = view[f.a()];
            var b = view[f.b()];
            var c = view[f.c()];
            var diffuse = Math.max(0, faceNormal(a, b, c).dot(lightDir)) * lightIntensity;
            var center = a.plus(b).plus(c).scale(1.0 / 3.0);
            var toLight = spotlightPos.minus(center).normalize();
            var distance = spotlightPos.minus(center).len();
            var attenuation = 1d / (1d + distance * distance);
            var normal = faceNormal(a, b, c);
            diffuse += Math.max(0, normal.dot(toLight)) * pointlight.intensity() * attenuation;
            fillFace(a, b, c, shade(instance.color(), AMBIENT + diffuse));
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
    private void fillFace(Vec3d a, Vec3d b, Vec3d c, int color) {
        var poly = Clip.clipNear(List.of(a, b, c), NEAR);
        if (poly.size() < 3) return;

        var v0 = poly.getFirst();
        var p0 = project(v0);
        for (var i = 1; i < poly.size() - 1; i++) {
            var v1 = poly.get(i);
            var v2 = poly.get(i + 1);
            var p1 = project(v1);
            var p2 = project(v2);
            triangle(sx(p0), sy(p0), 1 / v0.z(), sx(p1), sy(p1), 1 / v1.z(), sx(p2), sy(p2), 1 / v2.z(), color);
        }
    }

    /** Fills a screen-space triangle using edge functions. */
    private void triangle(int x0, int y0, double z0, int x1, int y1, double z1, int x2, int y2, double z2, int color) {
        // find the smallest rectangle that fits the triangle
        var minX = Math.max(0, Math.min(x0, Math.min(x1, x2)));
        var maxX = Math.min(getFb().getWidth() - 1, Math.max(x0, Math.max(x1, x2)));
        var minY = Math.max(0, Math.min(y0, Math.min(y1, y2)));
        var maxY = Math.min(getFb().getHeight() - 1, Math.max(y0, Math.max(y1, y2)));

        var area = edge(x0, y0, x1, y1, x2, y2);
        if (area == 0) return;

        // walk through the rectangle
        for (var y = minY; y <= maxY; y++) {
            for (var x = minX; x <= maxX; x++) {
                // cross-products
                var w0 = edge(x1, y1, x2, y2, x, y);
                var w1 = edge(x2, y2, x0, y0, x, y);
                var w2 = edge(x0, y0, x1, y1, x, y);

                // when cross-products signs match, the pixel is inside the triangle
                if ((w0 >= 0 && w1 >= 0 && w2 >= 0) || (w0 <= 0 && w1 <= 0 && w2 <= 0)) {
                    var z = (w0 * z0 + w1 * z1 + w2 * z2) / area;
                    var idx = y * getFb().getWidth() + x;
                    if (z > depth[idx]) {
                        depth[idx] = z;
                        getFb().set(x, y, color);
                    }
                }
            }
        }
    }

    /** Cross product. TODO: maybe instead use objects or something */
    private int edge(int ax, int ay, int bx, int by, int cx, int cy) {
        return (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
    }

    /** 3D cross product. */
    private Vec3d faceNormal(Vec3d a, Vec3d b, Vec3d c) {
        return b.minus(a).cross(c.minus(a)).normalize();
    }

    /** Projects a world point to view coordinates. */
    private Vec2d project(Vec3d p) {
        return Vec2d.of(getFocal() * p.x() / (p.z() * getAspect()),
                        getFocal() * p.y() / p.z());
    }

    /** Maps a projected point's x in [-1, 1] to a pixel column. */
    private int sx(Vec2d p) {
        return (int) (getFb().getWidth() * (p.x() + 1) / 2);
    }

    /** Maps a projected point's y in [-1, 1] to a pixel row. */
    public int sy(Vec2d p) {
        return (int) (getFb().getHeight() * (1 - p.y()) / 2);
    }

    /** Mask shenanigans TODO: make this less obscure */
    private int shade(int rgb, double f) {
        var r = clamp((int) (((rgb >> 16) & 0xFF) * f));
        var g = clamp((int) (((rgb >> 8) & 0xFF) * f));
        var b = clamp((int) (((rgb & 0xFF) * f)));
        return (r << 16) | (g << 8) | b;
    }

    /** Clamp to [0, 255] */
    private int clamp(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
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
