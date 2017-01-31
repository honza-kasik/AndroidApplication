package cz.honzakasik.geography.common.location.map;

import android.support.annotation.NonNull;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.view.MapView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.country.TerritoryPolygon;

import static cz.honzakasik.geography.common.location.map.PaintUtils.getRandomColor;

public class CountryPolygonOverlayHandler {

    private Map<Country, CountryPolygonOverlay> currentOverlay;
    private List<CountryPolygonOverlay> highlightedOverlays;

    private MapView mapView;

    public CountryPolygonOverlayHandler(MapView mapView) {
        this.mapView = mapView;
        this.currentOverlay = new ConcurrentHashMap<>();
    }

    private void addOverlay(Country country, int color) {
        CountryPolygonOverlay overlay = createCountryOverlay(country, color);
        //add layers
        this.mapView.getLayerManager().getLayers().addAll(overlay.getOverlayLayers());
        //add reference
        this.currentOverlay.put(country, overlay);
    }

    private CountryPolygonOverlay createCountryOverlay(Country country, int color) {
        List<CountryPolygon> overlay = new LinkedList<>();
        for (TerritoryPolygon polygon : country.getTerritory().getPolygons()) {
            overlay.add(drawPolygon(polygon, color));
        }
        return new CountryPolygonOverlay(overlay);
    }

    private CountryPolygon drawPolygon(@NonNull TerritoryPolygon polygon, int fillColor) {
        CountryPolygon polygonOverlay = new CountryPolygon(
                fillColor, null, AndroidGraphicFactory.INSTANCE);
        polygonOverlay.getLatLongs().addAll(polygon.getCoordinates());
        return polygonOverlay;
    }

    public void colorCountriesWithRandomColors(Iterable<Country> countries) {
        for (Country country : countries) {
            addOverlay(country, getRandomColor());
        }
    }

    private void clearHighlight(CountryPolygonOverlay overlay) {
        overlay.setHighlighted(false);
    }

    private void clearAllHighlights() {
        for (Country country : this.currentOverlay.keySet()) {
            CountryPolygonOverlay overlay = this.currentOverlay.get(country);
            if (overlay.isHighlighted()) {
                clearHighlight(overlay);
            }
        }
    }

    public void forceClearAllHighlights() {
        for (Country country : this.currentOverlay.keySet()) {
            clearHighlight(this.currentOverlay.get(country));
        }
    }

    public void forceClearOtherHighlights(Country ignoredCountry) {
        for (Country country : this.currentOverlay.keySet()) {
            CountryPolygonOverlay overlay = this.currentOverlay.get(country);
            if (!country.equals(ignoredCountry)) {
                clearHighlight(overlay);
            }
        }
    }

    public void highlightCountryOverlay(Country country) {
        clearAllHighlights();
        this.currentOverlay.get(country).setHighlighted(true);
        forceClearOtherHighlights(country);
    }

    public void highlightCountryOverlay(Country country, int fillColor) {
        clearAllHighlights();
        this.currentOverlay.get(country).setHighlighted(true, fillColor);
        forceClearOtherHighlights(country);
    }

    public void highlightCountryOverlay(Country country, int fillColor, long duration) {
        clearAllHighlights();
        this.currentOverlay.get(country).setHighlighted(true, fillColor, duration);
        forceClearOtherHighlights(country);
    }

    public void blinkAndHighlightCountryOverlay(Country country, int fillColor, long duration) {
        forceClearOtherHighlights(country);
        this.currentOverlay.get(country).blinkAndHighlight(fillColor, duration);
        forceClearOtherHighlights(country);
    }

    public void highLightCountryOverlayWithoutClearing(Country country, int fillColor, long duration) {
        if (this.highlightedOverlays == null) {
            this.highlightedOverlays = new LinkedList<>();
        }
        CountryPolygonOverlay overlay = this.currentOverlay.get(country);
        overlay.setHighlighted(true, fillColor, duration);
        this.highlightedOverlays.add(overlay);
    }

    public void blinkAndHighLightCountryOverlayWithoutClearing(Country country, int fillColor, long duration) {
        if (this.highlightedOverlays == null) {
            this.highlightedOverlays = new LinkedList<>();
        }
        CountryPolygonOverlay overlay = this.currentOverlay.get(country);
        overlay.blinkAndHighlight(fillColor, duration);
        this.highlightedOverlays.add(overlay);
    }

    public void clearAllStackedHighlights() {
        for (CountryPolygonOverlay overlay : this.highlightedOverlays) {
            clearHighlight(overlay);
        }
        this.highlightedOverlays.clear();
    }

}
