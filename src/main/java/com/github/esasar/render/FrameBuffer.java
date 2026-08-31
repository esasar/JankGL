package com.github.esasar.render;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public final class FrameBuffer {
    private final int width, height;
    private final int[] pixels;
    private final BufferedImage image;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public BufferedImage getImage() { return image; }

    /**
     * Sets the frame buffer to a solid {@code color}.
     *
     * @param color
     *              {@code color} to set buffer to.
     */
    public void clear(int color) {
        Arrays.fill(pixels, color);
    }

    /**
     * Sets pixel at given screen coordinates to a {@code color}.
     *
     * @param x
     *          screen x-coordinate.
     * @param y
     *          screen y-coordinate.
     * @param color
     *          color to set the pixel to.
     */
    public void set(int x, int y, int color) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        pixels[y * width + x] = color;
    }
}
