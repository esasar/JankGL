package com.github.esasar.scene;

import com.github.esasar.math.Vec3;

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
public record Mesh(List<Vec3> vertices, List<Edge> edges, List<Face> faces) {

    /** Edge of a mesh. Edge goes from vertex a to vertex b. */
    public record Edge(int a, int b) {
        public static Edge of(int a, int b) { return new Edge(a, b); }
    }

    /** Face of a mesh. */
    public record Face(int a, int b, int c) {
        public static Face of(int a, int b, int c) { return new Face(a, b, c); }
    }

    public static Mesh of(List<Vec3> vertices, List<Edge> edges, List<Face> faces) { return new Mesh(vertices, edges, faces); }
    public static Mesh of(List<Vec3> vertices, List<Edge> edges) { return of(vertices, edges, List.of()); }

    /** Return a cube mesh that has edge length l. */
    public static Mesh cube(double l) {
        var h = l / 2;
        var vs = List.of(Vec3.of(-h, -h, -h), Vec3.of(h, -h, -h),
                         Vec3.of(h, h, -h), Vec3.of(-h, h, -h),
                         Vec3.of(-h, -h, h), Vec3.of(h, -h, h),
                         Vec3.of(h, h, h), Vec3.of(-h, h, h));
        var es = List.of(Edge.of(0, 1), Edge.of(1, 2), Edge.of(2, 3), Edge.of(3, 0),
                         Edge.of(4, 5), Edge.of(5, 6), Edge.of(6, 7), Edge.of(7, 4),
                         Edge.of(0, 4), Edge.of(1, 5), Edge.of(2, 6), Edge.of(3, 7));
        return Mesh.of(vs, es);
    }

    public static Mesh plane(double w, double h, int g) {
        var vs = new ArrayList<Vec3>();
        var es = new ArrayList<Edge>();

        var c = 0;
        for (var i = -w / 2; i <= w / 2; i += w / g) {
            vs.add(Vec3.of(i, h / 2, 0));
            vs.add(Vec3.of(i, -h / 2, 0));
            es.add(Edge.of(c++, c++));
        }

        for (var i = -h / 2; i <= h / 2; i += h / g) {
            vs.add(Vec3.of(- w / 2, i, 0));
            vs.add(Vec3.of(w / 2, i, 0));
            es.add(Edge.of(c++, c++));
        }

        return Mesh.of(vs, es);
    }

    public static Mesh filledPlane(double w, double h) {
        var vs = List.of(Vec3.of(-h / 2, -w / 2, 0), Vec3.of(-h / 2, w / 2, 0),
                         Vec3.of( h / 2, w / 2, 0), Vec3.of(h / 2, - w / 2, 0));
        var fs = List.of(Face.of(0, 1, 2), Face.of(0, 2, 3));
        return Mesh.of(vs, List.of(), fs);
    }

    public static Mesh arrow(double  l) {
        var a = 0.01;
        var vs = List.of(Vec3.of(0, 0, 0), Vec3.of(0, l, 0),
                         Vec3.of(-a, l - a, 0), Vec3.of(a, l - a, 0));
        var es = List.of(Edge.of(0, 1),
                         Edge.of(1, 2), Edge.of(1, 3));
        return Mesh.of(vs, es);
    }

    public static Mesh test() {
        var vs = List.of(Vec3.of(0, 0, 0), Vec3.of(0, 1, 0), Vec3.of(1, 0, 0));
        var es = List.of(Edge.of(0, 1), Edge.of(1, 2), Edge.of(2, 0));
        var fs = List.of(Face.of(0, 1, 2));
        return Mesh.of(vs, es, fs);
    }
}
