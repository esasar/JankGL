package com.github.esasar;

import com.github.esasar.render.Camera;

import javax.swing.*;
import java.awt.*;

public class StatDisplay extends JPanel {

    private long frameTimeNs;
    private Camera camera;

    public StatDisplay() {
        setOpaque(false);
        setPreferredSize(new Dimension(180, 50));
    }

    public void update(long frameTimeNs, Camera camera) {
        this.frameTimeNs = frameTimeNs;
        this.camera = camera;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (camera == null) {
            return;
        }
        var g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(5, 5, 170, 40, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        var frameTimeMs = frameTimeNs / 1_000_000d;

        g2.drawString(String.format("Frame time: %.0f ms", frameTimeMs), 10, 21);
        g2.drawString("Camera: " + camera.position(), 10, 37);

        g2.dispose();
    }

    public static long measureTime(Runnable task) {
        var start = System.nanoTime();
        task.run();
        var end = System.nanoTime();
        return end - start;
    }
}
