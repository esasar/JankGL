package com.github.esasar.scene;

import com.github.esasar.math.Vec3;

/** Describes an entity. */
public record Instance(Mesh mesh, Vec3 position, double yaw, double pitch, double roll, int color) {

    /** Converts position of the entity to world coordinates. */
    public Vec3 toWorld(Vec3 local) {
        return local.rotateXZ(yaw()).rotateYZ(pitch()).rotateXY(roll()).plus(position());
    }
}
