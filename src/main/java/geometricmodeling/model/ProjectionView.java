/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

public enum ProjectionView {
    FRONT("front"),
    TOP("top"),
    SIDE("side");

    private String name;

    ProjectionView(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
