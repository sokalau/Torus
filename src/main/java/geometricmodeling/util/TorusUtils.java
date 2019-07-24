/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.util;

import geometricmodeling.model.Plane;
import geometricmodeling.model.SortOrder;

import java.util.Comparator;
import java.util.List;

public final class TorusUtils {
    private TorusUtils() {
    }

    public static void sortFacets(List<Plane> facets, SortOrder sortOrder) {
        switch (sortOrder) {
            case X_ASCENDING:
                facets.sort(Comparator.comparingDouble((Plane f) -> f.getCenter().getX())
                        .thenComparingDouble((Plane f) -> f.getCenter().getY())
                        .thenComparingDouble((Plane f) -> f.getCenter().getZ()));
                break;
            case X_DESCENDING:
                facets.sort(Comparator.comparingDouble((Plane f) -> f.getCenter().getX()).reversed()
                        .thenComparingDouble((Plane f) -> f.getCenter().getY())
                        .thenComparingDouble((Plane f) -> f.getCenter().getZ()));
                break;
            case Y_ASCENDING:
                facets.sort(Comparator.comparingDouble((Plane f) -> f.getCenter().getY())
                        .thenComparingDouble((Plane f) -> f.getCenter().getZ())
                        .thenComparingDouble((Plane f) -> f.getCenter().getX()));
                break;
            case Y_DESCENDING:
                facets.sort(Comparator.comparingDouble((Plane f) -> f.getCenter().getY()).reversed()
                        .thenComparingDouble((Plane f) -> f.getCenter().getZ())
                        .thenComparingDouble((Plane f) -> f.getCenter().getX()));
                break;
            case Z_ASCENDING:
                facets.sort(Comparator.comparingDouble((Plane f) -> f.getCenter().getZ())
                        .thenComparingDouble((Plane f) -> f.getCenter().getX())
                        .thenComparingDouble((Plane f) -> f.getCenter().getY()));
                break;
            case Z_DESCENDING:
                facets.sort(Comparator.comparingDouble((Plane f) -> f.getCenter().getZ()).reversed()
                        .thenComparingDouble((Plane f) -> f.getCenter().getX())
                        .thenComparingDouble((Plane f) -> f.getCenter().getY()));
                break;
            default:
                throw new IllegalArgumentException("There is no such sort order.");
        }
    }
}
