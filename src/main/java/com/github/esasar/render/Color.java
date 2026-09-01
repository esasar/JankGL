package com.github.esasar.render;

public enum Color {

    GREEN(0x00FF00),
    RED(0xFF6030),
    BLACK(0x000000),
    BLUE(0x0000FF),
    YELLOW(0xFFFF00);

    private final int value;

    Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
