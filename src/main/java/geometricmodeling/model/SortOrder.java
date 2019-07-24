/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

public enum SortOrder {
    Z_ASCENDING("z_ascending"),
    Z_DESCENDING("z_descending"),
    Y_ASCENDING("y_ascending"),
    Y_DESCENDING("y_descending"),
    X_ASCENDING("x_ascending"),
    X_DESCENDING("x_descending");

    private String name;

    SortOrder(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
