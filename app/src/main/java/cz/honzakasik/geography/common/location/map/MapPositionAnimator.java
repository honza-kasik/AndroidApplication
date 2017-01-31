package cz.honzakasik.geography.common.location.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.model.MapViewPosition;

public class MapPositionAnimator {

    private final MapView mapView;
    private final LatLongEvaluator latLongEvaluator;

    public MapPositionAnimator(MapView mapView) {
        this.mapView = mapView;
        this.latLongEvaluator = new LatLongEvaluator();
    }

    public void animateToCoordinates(LatLong coordinates, long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(latLongEvaluator,
                this.mapView.getModel().mapViewPosition.getCenter(),
                coordinates);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MapViewPosition mapViewPosition = mapView.getModel().mapViewPosition;
                byte zoom = mapViewPosition.getZoomLevel();
                mapViewPosition.setMapPosition(
                        new MapPosition((LatLong) animation.getAnimatedValue(), zoom));
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void animateToZoomLevel(byte zoom, long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(
                this.mapView.getModel().mapViewPosition.getZoomLevel(), zoom);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mapView.getModel()
                        .mapViewPosition
                        .setZoomLevel((byte) ((int)animation.getAnimatedValue()), true);
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void animateToPosition(final MapPosition mapPosition, final long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(latLongEvaluator,
                this.mapView.getModel().mapViewPosition.getCenter(),
                mapPosition.latLong);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MapViewPosition mapViewPosition = mapView.getModel().mapViewPosition;
                byte zoom = mapViewPosition.getZoomLevel();
                mapViewPosition.setMapPosition(
                        new MapPosition((LatLong) animation.getAnimatedValue(), zoom));
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //mapView.getModel().mapViewPosition.setZoomLevel(mapPosition.zoomLevel, true);
                animateToZoomLevel(mapPosition.zoomLevel, duration);
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private final class LatLongEvaluator implements TypeEvaluator<LatLong> {

        @Override
        public LatLong evaluate(float fraction, LatLong startValue, LatLong endValue) {
            double latStart = startValue.latitude;
            double lonStart = startValue.longitude;

            double latEnd = endValue.latitude;
            double lonEnd = endValue.longitude;

            double latFraction = latStart + fraction * (latEnd - latStart);
            double lonFraction = lonStart + fraction * (lonEnd - lonStart);

            return new LatLong(latFraction, lonFraction);
        }
    }
}
