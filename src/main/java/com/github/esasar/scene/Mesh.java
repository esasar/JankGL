package com.github.esasar.scene;

import com.github.esasar.math.Vec2d;
import com.github.esasar.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a mesh.
 *
 * @param vertices
 *              vertices of the mesh.
 * @param edges
 *              edges of the mesh.
 */
public record Mesh(List<Vec3d> vertices, List<Edge> edges, List<Face> faces) {

    /** Edge of a mesh. Edge goes from vertex a to vertex b. */
    public record Edge(int a, int b) {
        public static Edge of(int a, int b) { return new Edge(a, b); }
    }

    /** Face of a mesh. */
    public record Face(int a, int b, int c) {
        public static Face of(int a, int b, int c) { return new Face(a, b, c); }
    }

    public static Mesh of(List<Vec3d> vertices, List<Edge> edges, List<Face> faces) { return new Mesh(vertices, edges, faces); }
    public static Mesh of(List<Vec3d> vertices, List<Edge> edges) { return of(vertices, edges, List.of()); }

    /** Return a cube mesh that has edge length l. */
    public static Mesh cube(double l) {
        var h = l / 2;
        var vs = List.of(Vec3d.of(-h, -h, -h), Vec3d.of(h, -h, -h),
                         Vec3d.of(h, h, -h), Vec3d.of(-h, h, -h),
                         Vec3d.of(-h, -h, h), Vec3d.of(h, -h, h),
                         Vec3d.of(h, h, h), Vec3d.of(-h, h, h));
        var es = List.of(Edge.of(0, 1), Edge.of(1, 2), Edge.of(2, 3), Edge.of(3, 0),
                         Edge.of(4, 5), Edge.of(5, 6), Edge.of(6, 7), Edge.of(7, 4),
                         Edge.of(0, 4), Edge.of(1, 5), Edge.of(2, 6), Edge.of(3, 7));
        return Mesh.of(vs, es);
    }

    public static Mesh filledCube(double l) {
        var h = l / 2;
        var vs = List.of(Vec3d.of(-h, -h, -h), Vec3d.of(h, -h, -h),
                         Vec3d.of(h, h, -h), Vec3d.of(-h, h, -h),
                         Vec3d.of(-h, -h, h), Vec3d.of(h, -h, h),
                         Vec3d.of(h, h, h), Vec3d.of(-h, h, h));
        var fs = List.of(Face.of(0, 1, 5), Face.of(0, 4, 5),
                         Face.of(0, 1, 2), Face.of(0, 2, 3),
                         Face.of(1, 2, 5), Face.of(2, 5, 6),
                         Face.of(0, 3, 4), Face.of(3, 4, 7),
                         Face.of(4, 5, 6), Face.of(4, 6, 7),
                         Face.of(2, 3, 6), Face.of(3, 6, 7));
        return Mesh.of(vs, List.of(), fs);
    }

    public static Mesh sphere(double radius, int detail) {
        var vs = new ArrayList<Vec3d>();
        for (var i = 0; i <= detail; i++) {
            var phi = Math.PI * i / detail;
            var sp = Math.sin(phi);
            var cp = Math.cos(phi);
            for (var j = 0; j < detail; j++) {
                var theta = 2 * Math.PI * j / detail;
                var st = Math.sin(theta);
                var ct = Math.cos(theta);
                vs.add(Vec3d.of(radius * sp * ct,
                                radius * cp,
                                radius * sp * st));
            }
        }

        var fs = new ArrayList<Face>();
        for (var i = 0; i < detail; i++) {
            for (var j = 0; j < detail; j++) {
                var j2 = (j + 1) % detail;
                var a = i * detail + j;
                var b = i * detail + j2;
                var c = (i + 1) * detail + j;
                var d = (i + 1) * detail + j2;

                if (i != 0) fs.add(Face.of(a, b, c));
                if (i != detail - 1) fs.add(Face.of(b, d, c));
            }
        }

        return Mesh.of(vs, List.of(), fs);
    }

    public static Mesh plane(double w, double h, int g) {
        var vs = new ArrayList<Vec3d>();
        var es = new ArrayList<Edge>();

        var c = 0;
        for (var i = -w / 2; i <= w / 2; i += w / g) {
            vs.add(Vec3d.of(i, h / 2, 0));
            vs.add(Vec3d.of(i, -h / 2, 0));
            es.add(Edge.of(c++, c++));
        }

        for (var i = -h / 2; i <= h / 2; i += h / g) {
            vs.add(Vec3d.of(- w / 2, i, 0));
            vs.add(Vec3d.of(w / 2, i, 0));
            es.add(Edge.of(c++, c++));
        }

        return Mesh.of(vs, es);
    }

    public static Mesh filledPlane(double w, double h) {
        var vs = List.of(Vec3d.of(-h / 2, -w / 2, 0), Vec3d.of(-h / 2, w / 2, 0),
                         Vec3d.of(h / 2, w / 2, 0), Vec3d.of(h / 2, - w / 2, 0));
        var fs = List.of(Face.of(0, 1, 2), Face.of(0, 2, 3));
        return Mesh.of(vs, List.of(), fs);
    }

    public static Mesh arrow(double  l) {
        var a = 0.01;
        var vs = List.of(Vec3d.of(0, 0, 0), Vec3d.of(0, l, 0),
                         Vec3d.of(-a, l - a, 0), Vec3d.of(a, l - a, 0));
        var es = List.of(Edge.of(0, 1),
                         Edge.of(1, 2), Edge.of(1, 3));
        return Mesh.of(vs, es);
    }

    public static Mesh test() {
        var vs = List.of(Vec3d.of(0, 0, 0), Vec3d.of(0, 1, 0), Vec3d.of(1, 0, 0));
        var es = List.of(Edge.of(0, 1), Edge.of(1, 2), Edge.of(2, 0));
        var fs = List.of(Face.of(0, 1, 2));
        return Mesh.of(vs, es, fs);
    }
}
