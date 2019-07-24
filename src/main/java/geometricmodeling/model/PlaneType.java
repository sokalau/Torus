/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

public enum PlaneType {
    XOY("XOY"),
    XOZ("XOZ"),
    ZOY("ZOY");

    private String name;

    PlaneType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
