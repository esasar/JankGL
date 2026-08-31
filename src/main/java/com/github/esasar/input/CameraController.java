package com.github.esasar.input;

import com.github.esasar.render.Camera;

import java.awt.event.KeyEvent;

/** Updates camera based on inputs. */
public final class CameraController {
    private static final double MOVE_SPEED = 1.5d;
    private static final double TURN_SPEED = 1.5d;
    private static final double MAX_PITCH = Math.PI / 2 - 0.01;

    public Camera update(Camera camera, Keyboard keyboard, double dt) {
        var move = MOVE_SPEED * dt;
        var turn = TURN_SPEED * dt;

        var pos = camera.position();
        if (keyboard.down(KeyEvent.VK_W)) pos = pos.plus(camera.forward().scale(move));
        if (keyboard.down(KeyEvent.VK_S)) pos = pos.plus(camera.forward().scale(-move));
        if (keyboard.down(KeyEvent.VK_D)) pos = pos.plus(camera.right().scale(move));
        if (keyboard.down(KeyEvent.VK_A)) pos = pos.plus(camera.right().scale(-move));

        var yaw = camera.yaw();
        if (keyboard.down(KeyEvent.VK_Q)) yaw += turn;
        if (keyboard.down(KeyEvent.VK_E)) yaw -= turn;

        var pitch = camera.pitch();
        if (keyboard.down(KeyEvent.VK_R)) pitch -= turn;
        if (keyboard.down(KeyEvent.VK_F)) pitch += turn;
        pitch = Math.max(-MAX_PITCH, Math.min(MAX_PITCH, pitch));

        return Camera.of(pos, yaw, pitch);
    }
}
