package com.github.esasar;

import com.github.esasar.input.CameraController;
import com.github.esasar.input.Keyboard;
import com.github.esasar.math.Vec3;
import com.github.esasar.render.Camera;
import com.github.esasar.render.Color;
import com.github.esasar.render.FrameBuffer;
import com.github.esasar.render.Renderer;
import com.github.esasar.scene.Instance;
import com.github.esasar.scene.Mesh;
import com.github.esasar.scene.Scene;

import javax.swing.*;
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
        var cube = Mesh.cube(.5);
        var arrow = Mesh.arrow(.5);
        var scene = Scene.of(List.of(new Instance(Mesh.filledPlane(10, 10), Vec3.ORIGO.translateY(-1), 0, Math.PI / 2, 0, Color.GREEN.getValue()),
                                     //new Instance(arrow, Vec3.ORIGO, 0, 0, 0, Color.GREEN.getValue()),
                                     //new Instance(arrow, Vec3.ORIGO, 0, 0, Math.PI / 2, Color.RED.getValue()),
                                     //new Instance(arrow, Vec3.ORIGO, 0, Math.PI / 2, 0, Color.BLUE.getValue()),
                                     new Instance(cube, Vec3.of(0, 0, 2), 0, 0, 0, Color.RED.getValue())
                                     //new Instance(cube, Vec3.of(1.2, 0, 3), 0.6, 0, 0, Color.GREEN.getValue()),
                                     //new Instance(Mesh.plane(10, 10, 10), Vec3.of(0, -1, 0), 0, Math.PI / 2, 0, Color.GREEN.getValue())
        ));

        // TODO: effectively final
        Camera[] camera = { new Camera(Vec3.of(0, 0, 0), 0, 0) };

        var display = new Display(fb, SCALE, keyboard);
        var frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(display);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        display.requestFocusInWindow();

        new Timer((int) (DT * 1000), _ -> {
            camera[0] = controller.update(camera[0], keyboard, DT);
            renderer.render(scene, camera[0]);
            display.repaint();
        }).start();
    }
}
