package cz.honzakasik.geography.common.location.country;

import org.mapsforge.core.model.BoundingBox;

import java.util.LinkedList;
import java.util.List;

/**
 * Country territory abstraction class.
 */
public class Territory {
    private final List<BoundingBox> boundingBoxes;
    private final List<TerritoryPolygon> polygons;

    private Territory(Builder builder) {
        this.boundingBoxes = builder.boundingBoxes;
        this.polygons = builder.polygons;
    }

    /**
     * Determines whether given point belongs to this territory.
     * @param latitude Latitude of point
     * @param longitude Longitude of point
     * @return true if point is inside this territory, false otherwise
     */
    public boolean contains(double latitude, double longitude) {
        for (TerritoryPolygon polygon : polygons) {
            if (polygon.contains(latitude, longitude)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether given point belongs to bounding box of this territory
     * @param latitude Latitude of point
     * @param longitude Longitude of point
     * @return true if point is inside bounding box of this territory, false otherwise
     */
    public boolean isPointInBoundingBox(double longitude, double latitude) {
        if (boundingBoxes != null) {
            for (BoundingBox boundingBox : boundingBoxes) {
                if (boundingBox.contains(latitude, longitude)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Obtain polygons which composes this territory
     * @return list of territory polygons
     */
    public List<TerritoryPolygon> getPolygons() {
        return polygons;
    }

    /**
     * Get bounding boxes of all territory polygons of this territory
     * @return list of bounding boxes
     */
    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public static class Builder {
        private List<BoundingBox> boundingBoxes;
        private List<TerritoryPolygon> polygons;

        public Builder polygon(TerritoryPolygon polygon) {
            if (polygons == null) {
                polygons = new LinkedList<>();
            }
            polygons.add(polygon);
            return this;
        }

        public Builder boundingBox(BoundingBox boundingBox) {
            if (boundingBoxes == null) {
                boundingBoxes = new LinkedList<>();
            }
            boundingBoxes.add(boundingBox);
            return this;
        }

        public Territory build() {
            return new Territory(this);
        }
    }
}
