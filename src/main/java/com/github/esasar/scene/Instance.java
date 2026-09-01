package com.github.esasar.scene;

import com.github.esasar.math.Vec3d;

/** Describes an entity. */
public record Instance(Mesh mesh, Vec3d position, double yaw, double pitch, double roll, int color) {

    /** Converts position of the entity to world coordinates. */
    public Vec3d toWorld(Vec3d local) {
        return local.rotateXZ(yaw()).rotateYZ(pitch()).rotateXY(roll()).plus(position());
    }
}
