/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

public enum Projection {
    ORTHOGONAL("orthogonal"),
    PERSPECTIVE("perspective"),
    AXONOMETRIC("axonometric"),
    OBLIQUE("oblique");

    private String name;

    Projection(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
