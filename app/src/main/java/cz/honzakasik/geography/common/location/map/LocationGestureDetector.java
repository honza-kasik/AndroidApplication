package cz.honzakasik.geography.common.location.map;

import android.view.GestureDetector;
import android.view.MotionEvent;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.util.MapViewProjection;
import org.mapsforge.map.view.MapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.common.location.LocationManager;
import cz.honzakasik.geography.common.location.PointNotInAnyCountryException;
import cz.honzakasik.geography.common.location.country.Country;

public final class LocationGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private Logger logger = LoggerFactory.getLogger(LocationGestureDetector.class);

    private final LocationManager locationManager = new LocationManager(App.getContext());
    private final MapView mapView;
    private final OnTouchHook hook;
    private boolean isTouchDisabled;

    public LocationGestureDetector(MapView mapView, OnTouchHook hook) {
        this.mapView = mapView;
        this.hook = hook;
    }

    public void setIsTouchDisabled(boolean isTouchDisabled) {
        this.isTouchDisabled = isTouchDisabled;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isTouchDisabled) {
            return false;
        }
        LatLong latLong = new MapViewProjection(mapView).fromPixels(e.getX(), e.getY());
        if (latLong == null) {
            return false;
        }
        logger.info("Clicked coordinates are " + latLong.toString());
        try {
            Country country = locationManager.inWhichCountryPointIs(latLong);
            logger.info("Clicked coordinates are inside {}", country.getName());
            hook.afterClickedCountry(country);

        } catch (PointNotInAnyCountryException exception) {
            logger.error(exception.getMessage());
        }
        return true;
    }

    public interface OnTouchHook {

          void afterClickedCountry(Country country);

    }

    /*@Override
    public void onLongPress(MotionEvent e) {
        if (isTouchDisabled) {
            return;
        }
        LatLong latLong = new MapViewProjection(mapView).fromPixels(e.getX(), e.getY());
        if (latLong == null) {
            return;
        }
        logger.info("Clicked coordinates are " + latLong.toString());
        try {
            Country country = locationManager.inWhichCountryPointIs(latLong);
            logger.info("Clicked coordinates are inside {}", country.getName());
            hook.afterClickedCountry(country);

        } catch (PointNotInAnyCountryException exception) {
            logger.error(exception.getMessage());
        }
    }*/
}
