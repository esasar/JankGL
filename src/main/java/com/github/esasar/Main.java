package com.github.esasar;

import com.github.esasar.input.CameraController;
import com.github.esasar.input.Keyboard;
import com.github.esasar.math.Vec3d;
import com.github.esasar.render.*;
import com.github.esasar.render.Color;
import com.github.esasar.render.Renderer;
import com.github.esasar.scene.Instance;
import com.github.esasar.scene.Mesh;
import com.github.esasar.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    private static final int WIDTH = 400, HEIGHT = 300, SCALE = 2;
    private static final int FPS = 60;
    private static final double DT = 1d / FPS;

    static void main() {
        SwingUtilities.invokeLater(Main::run);
    }

    private static void run() {
        var fb = new FrameBuffer(WIDTH, HEIGHT);
        var renderer = new Renderer(fb, Math.toRadians(60));
        var keyboard = new Keyboard();
        var controller = new CameraController();

        // place some instances
        var sphere = Mesh.sphere(.5, 200);
        var smallSphere = Mesh.sphere(.01, 50);
        var arrow = Mesh.arrow(.5);
        var scene = Scene.of(List.of(new Instance(Mesh.filledPlane(10, 10), Vec3d.ORIGO.translateY(-1), 0, Math.PI / 2, 0, Color.GREEN.getValue()),
                                     new Instance(smallSphere, Vec3d.of(0, 0, 0), 0, 0, 0, Color.YELLOW.getValue()),
                                     //new Instance(arrow, Vec3.ORIGO, 0, 0, Math.PI / 2, Color.RED.getValue()),
                                     //new Instance(arrow, Vec3.ORIGO, 0, Math.PI / 2, 0, Color.BLUE.getValue()),
                                     new Instance(sphere, Vec3d.of(0, 0, 2), 0, 0, 0, Color.RED.getValue())
                                     //new Instance(cube, Vec3.of(1.2, 0, 3), 0.6, 0, 0, Color.GREEN.getValue()),
                                     //new Instance(Mesh.plane(10, 10, 10), Vec3.of(0, -1, 0), 0, Math.PI / 2, 0, Color.GREEN.getValue())
        ));

        var light = new DirectionalLight(Vec3d.of(0.4, 1, -0.3), 0.2);
        var pointLight = new PointLight(Vec3d.of(0, 0, 0), 1);

        // TODO: effectively final
        Camera[] camera = { new Camera(Vec3d.of(0, 0, 0), 0, 0) };

        var display = new Display(fb, SCALE, keyboard);
        var stats = new StatDisplay();

        var layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        display.setBounds(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
        stats.setBounds(5, 5, 180, 50);
        layeredPane.add(display, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(stats, JLayeredPane.PALETTE_LAYER);

        var frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(layeredPane);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        display.requestFocusInWindow();

        new Timer((int) (DT * 1000), _ -> {
            var frameTime = StatDisplay.measureTime(() -> {
                camera[0] = controller.update(camera[0], keyboard, DT);
                renderer.render(scene, camera[0], light, pointLight);
                display.repaint();
            });
            stats.update(frameTime, camera[0]);
        }).start();
    }
}
