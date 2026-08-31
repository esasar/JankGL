package com.github.esasar.scene;

import java.util.Collection;

/** Scene. */
public record Scene(Collection<Instance> instances) {
    public static Scene of(Collection<Instance> instances) {
        return new Scene(instances);
    }
}
