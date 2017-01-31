package cz.honzakasik.geography.common.location.map;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.util.MapViewProjection;
import org.mapsforge.map.view.MapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.LocationManager;
import cz.honzakasik.geography.common.location.PointNotInAnyCountryException;
import cz.honzakasik.geography.common.location.SlidingCountryView;
import cz.honzakasik.geography.common.location.country.Country;

@Deprecated
public class BlindMapOnTouchListener implements View.OnTouchListener {

    private Logger logger = LoggerFactory.getLogger(BlindMapOnTouchListener.class);

    private Context context;
    private MapView mapView;
    private LocationManager locationManager;
    private SlidingCountryView slidingLayer;
    private LinearLayout slidingContent;

    private CountryPolygonOverlayHandler polygonProvider;

    public BlindMapOnTouchListener(Context context, SlidingCountryView slidingLayer, MapView mapView,
                                   LocationManager locationManager, CountryPolygonOverlayHandler polygonProvider) {
        this.context = context;
        this.mapView = mapView;
        this.locationManager = locationManager;
        this.polygonProvider = polygonProvider;
        this.slidingLayer = slidingLayer;
        this.slidingContent = (LinearLayout) slidingLayer.findViewById(R.id.sliding_layer_content);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!this.slidingLayer.isClosed()) {
            this.slidingLayer.closeLayer(true);
            this.slidingContent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            LatLong latLong = new MapViewProjection(mapView).fromPixels(event.getX(), event.getY());
            if (latLong == null) {
                return false;
            }
            logger.info("Clicked coordinates are " + latLong.toString());
            try {
                Country country = locationManager.inWhichCountryPointIs(latLong);
                logger.info("Clicked coordinates are inside {}", country.getName());
                polygonProvider.highlightCountryOverlay(country);
                slidingLayer.openCountryPreview(country);

                slidingContent.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (slidingLayer.isInPreviewMode()) {
                            slidingLayer.openLayer(true);
                        }
                        return true;
                    }
                    return false;
                    }
                });

            } catch (PointNotInAnyCountryException e) {
                logger.error(e.getMessage());
            }
            return true;
        }
        return false;
    }
}
