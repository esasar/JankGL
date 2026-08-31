package com.github.esasar;

import com.github.esasar.input.Keyboard;
import com.github.esasar.render.FrameBuffer;

import javax.swing.*;
import java.awt.*;

public final class Display extends JPanel {
    private final FrameBuffer fb;

    public Display(FrameBuffer fb, int scale, Keyboard keys) {
        this.fb = fb;
        setPreferredSize(new Dimension(scale * fb.getWidth(), scale * fb.getHeight()));
        setFocusable(true);
        addKeyListener(keys);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                          RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(fb.getImage(), 0, 0, getWidth(), getHeight(), null);
    }
}
