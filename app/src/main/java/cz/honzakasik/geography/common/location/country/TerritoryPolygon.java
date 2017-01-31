package cz.honzakasik.geography.common.location.country;

import org.mapsforge.core.model.LatLong;

import java.util.List;

/**
 * This class is an abstraction over a piece of land which belong to a country. One country can have
 * multiple of these polygons.
 */
public class TerritoryPolygon {
    
    private final List<LatLong> coordinates;

    public TerritoryPolygon(List<LatLong> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Get polygon coordinates
     * @return list of polygon points
     */
    public List<LatLong> getCoordinates() {
        return coordinates;
    }

    //latitude = x, longitude = y

    /**
     * Return true if this polygon contains given point
     * @param latitude Latitude of point
     * @param longitude Longitude of point
     * @return true if point is inside this polygon, false otherwise
     */
    public boolean contains(double latitude, double longitude) {
        int vertexCount = coordinates.size();
        int i, j;
        boolean c = false;
        for (i = 0, j = vertexCount-1; i < vertexCount; j = i++) {

            double jLatitude = coordinates.get(j).latitude;
            double iLatitude = coordinates.get(i).latitude;
            double jLongitude = coordinates.get(j).longitude;
            double iLongitude = coordinates.get(i).longitude;

            if (((iLongitude > longitude) != (jLongitude > longitude)) &&
                (latitude < (jLatitude-iLatitude) * (longitude-iLongitude) / (jLongitude-iLongitude) + iLatitude) )
                c = !c;
        }
        return c;
    }
}
