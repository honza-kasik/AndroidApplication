package cz.honzakasik.geography.education.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;

import org.greenrobot.eventbus.EventBus;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.map.CountryPolygonOverlayHandler;
import cz.honzakasik.geography.common.location.map.LocationGestureDetector;
import cz.honzakasik.geography.common.location.map.TileRendererLayerBuilder;
import cz.honzakasik.geography.common.utils.FileHelper;
import cz.honzakasik.geography.common.utils.PropUtils;

public class LocationActivity extends AppCompatActivity {

    Logger logger = LoggerFactory.getLogger(LocationActivity.class);

    private MapView mapView;

    private static final byte DEFAULT_ZOOM_LEVEL = 4;
    private static final byte MAX_ZOOM_LEVEL = DEFAULT_ZOOM_LEVEL + 3;
    private static final byte MIN_ZOOM_LEVEL = DEFAULT_ZOOM_LEVEL - 1;
    private CountryPolygonOverlayHandler polygonOverlayHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.location_full);

        this.mapView = (MapView) findViewById(R.id.mapView);
        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.getModel().mapViewPosition
                .setMapLimit(BoundingBox.fromString(PropUtils.get("map.boundingbox")));

        polygonOverlayHandler = new CountryPolygonOverlayHandler(this.mapView);

        this.mapView.setGestureDetector(new GestureDetector(this,
                new LocationGestureDetector(this.mapView, new SlidingLayerOnTouchHook())));

        addBaseMapLayer(this.mapView);
        colorCountries(polygonOverlayHandler);
        setZoomLevels(this.mapView);
        this.mapView.getModel()
                .mapViewPosition
                .setMapPosition(
                        new MapPosition(
                                BoundingBox.fromString(PropUtils.get("map.boundingbox"))
                                        .getCenterPoint(),
                                DEFAULT_ZOOM_LEVEL));
        this.mapView.repaint();

        logger.info(
                "=========================\n" +
                "LocationActivity created!\n" +
                "=========================");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    private void addBaseMapLayer(MapView mapView) {
        TileRendererLayer tileRendererLayer = new TileRendererLayerBuilder()
                .mapDataStore(getFile(PropUtils.get("resources.maps.ocean.path")), false, false)
                .mapDataStore(getFile(PropUtils.get("resources.maps.admin.path")), false, false)
                .themeFile(getFile(PropUtils.get("resources.renderer.theme.path")))
                .setTransparent(true)
                .model(mapView.getModel())
                .context(this)
                .tileCacheName("myCache")
                .build();
        mapView.getLayerManager().getLayers().add(tileRendererLayer);
    }

    private void setZoomLevels(MapView mapView) {
        mapView.getModel().mapViewPosition.setZoomLevelMax(MAX_ZOOM_LEVEL);
        mapView.getModel().mapViewPosition.setZoomLevelMin(MIN_ZOOM_LEVEL);
        mapView.getMapZoomControls().setZoomLevelMin(MIN_ZOOM_LEVEL);
        mapView.getMapZoomControls().setZoomLevelMax(MAX_ZOOM_LEVEL);
    }

    private void colorCountries(CountryPolygonOverlayHandler overlayHandler) {
        overlayHandler.colorCountriesWithRandomColors(((App) getApplicationContext()).getCountries());
    }

    private File getFile(String filePath) {
        return new File(FileHelper.getApplicationExternalStoragePath(this), filePath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mapView.destroyAll();
    }

    private final class SlidingLayerOnTouchHook implements LocationGestureDetector.OnTouchHook {

        @Override
        public void afterClickedCountry(Country country) {
            polygonOverlayHandler.highlightCountryOverlay(country);
            openCountryInfoActivity(country);
        }
    }

    private void openCountryInfoActivity(Country country) {
        EventBus.getDefault().postSticky(country);
        Intent intent = new Intent(this, CountryInfoActivity.class);
        startActivity(intent);

    }

}
