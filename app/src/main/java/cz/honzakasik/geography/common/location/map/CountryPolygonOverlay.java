package cz.honzakasik.geography.common.location.map;

import android.support.annotation.NonNull;

import org.mapsforge.map.layer.Layer;

import java.util.LinkedList;
import java.util.List;

final class CountryPolygonOverlay {

    private List<CountryPolygon> countryOverlayPolygonLayers;

    public boolean isHighlighted() {
        return this.countryOverlayPolygonLayers.get(0).isHighlighted();
    }

    public void setHighlighted(boolean highlighted) {
        for (CountryPolygon polygon : this.countryOverlayPolygonLayers) {
            polygon.setHighlighted(highlighted);
        }
    }

    public void setHighlighted(boolean highlighted, int fillColor) {
        for (CountryPolygon polygon : this.countryOverlayPolygonLayers) {
            polygon.setHighlighted(highlighted, fillColor);
        }
    }

    public void setHighlighted(boolean highlighted, int fillColor, long duration) {
        for (CountryPolygon polygon : this.countryOverlayPolygonLayers) {
            polygon.setHighlighted(highlighted, fillColor, duration);
        }
    }

    public void blinkAndHighlight(int fillColor, long duration) {
        for (CountryPolygon polygon : this.countryOverlayPolygonLayers) {
            polygon.blinkAndHighlight(fillColor, duration);
        }
    }

    CountryPolygonOverlay(@NonNull List<CountryPolygon> countryOverlayPolygonLayers) {
        this.countryOverlayPolygonLayers = countryOverlayPolygonLayers;
    }

    public List<Layer> getOverlayLayers() {
        List<Layer> layers = new LinkedList<>();
        for (Layer layer : this.countryOverlayPolygonLayers) {
            layers.add(layer);
        }
        return layers;
    }

    public boolean isPainted() {
        for (CountryPolygon polygon : this.countryOverlayPolygonLayers) {
            if (polygon.isPainted()) {
                return true;
            }
        }
        return false;
    }
}
