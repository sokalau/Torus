/*
 * Copyright (c) 2019 Nikita Sokolov. All rights reserved.
 */

package geometricmodeling.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Torus {
    private double minorRadius;
    private double minorAngle;
    private double majorRadius;
    private double majorAngle;
    private List<Plane> facets = new ArrayList<>();

    private Torus() {
    }

    public static class Builder {
        private double minorRadius;
        private double minorAngle;
        private double majorRadius;
        private double majorAngle;
        private List<Plane> facets;

        public Builder() {
        }

        public Builder minorRadius(double minorRadius) {
            this.minorRadius = minorRadius;
            return this;
        }

        public Builder minorAngle(double minorAngle) {
            this.minorAngle = minorAngle;
            return this;
        }

        public Builder majorRadius(double majorRadius) {
            this.majorRadius = majorRadius;
            return this;
        }

        public Builder majorAngle(double majorAngle) {
            this.majorAngle = majorAngle;
            return this;
        }

        public Builder facets(List<Plane> facets) {
            this.facets = facets;
            return this;
        }

        public Torus build() {
            return new Torus(this);
        }
    }

    private Torus(Builder builder) {
        this.minorRadius = builder.minorRadius;
        this.minorAngle = builder.minorAngle;
        this.majorRadius = builder.majorRadius;
        this.majorAngle = builder.majorRadius;
        this.facets = builder.facets;
    }

    public double getMinorRadius() {
        return minorRadius;
    }

    public double getMinorAngle() {
        return minorAngle;
    }

    public double getMajorRadius() {
        return majorRadius;
    }

    public double getMajorAngle() {
        return majorAngle;
    }

    public List<Plane> getFacets() {
        return facets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Torus that = (Torus) o;
        return Double.compare(this.minorRadius, that.minorRadius) == 0 &&
                Double.compare(this.minorAngle, that.minorAngle) == 0 &&
                Double.compare(this.majorRadius, that.majorRadius) == 0 &&
                Double.compare(this.majorAngle, that.majorAngle) == 0 &&
                Objects.equals(this.facets, that.facets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minorRadius, minorAngle, majorRadius, majorAngle, facets);
    }

    @Override
    public String toString() {
        return "Torus{" +
                "minorRadius=" + minorRadius +
                ", minorAngle=" + minorAngle +
                ", majorRadius=" + majorRadius +
                ", majorAngle=" + majorAngle +
                ", facets=" + facets +
                "}";
    }
}
