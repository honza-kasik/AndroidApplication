package cz.honzakasik.geography.common.location.map;

import android.content.Context;
import android.support.annotation.NonNull;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;

import java.io.File;
import java.io.FileNotFoundException;

public class TileRendererLayerBuilder {

    private MultiMapDataStore multiMapDataStore;
    private String tileCacheName;
    private XmlRenderTheme theme;
    private boolean hasAlpha;
    private Model model;
    private boolean renderLabels;
    private Context context;

    public TileRendererLayerBuilder() {
    }

    public TileRendererLayerBuilder mapDataStore(@NonNull MapDataStore mapDataStore,
                                                 boolean useStartZoomLevel,
                                                 boolean useStartPosition) {
        if (multiMapDataStore == null) {
            multiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL);
        }
        multiMapDataStore.addMapDataStore(mapDataStore, useStartZoomLevel, useStartPosition);
        return this;
    }

    public TileRendererLayerBuilder mapDataStore(@NonNull File file, boolean useStartZoomLevel,
                                                 boolean useStartPosition) {
        MapDataStore mapDataStore = new MapFile(file);
        mapDataStore(mapDataStore, useStartZoomLevel, useStartPosition);
        return this;
    }

    public TileRendererLayerBuilder tileCacheName(@NonNull String tileCacheName) {
        this.tileCacheName = tileCacheName;
        return this;
    }

    public TileRendererLayerBuilder themeFile(@NonNull File themeFile) {
        XmlRenderTheme xmlRenderTheme = null;
        try {
            xmlRenderTheme = new ExternalRenderTheme(themeFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.theme = xmlRenderTheme;
        return this;
    }

    public TileRendererLayerBuilder setTransparent(boolean isTransparent) {
        this.hasAlpha = isTransparent;
        return this;
    }

    public TileRendererLayerBuilder model(@NonNull Model model) {
        this.model = model;
        return this;
    }

    public TileRendererLayerBuilder renderLabels() {
        this.renderLabels = true;
        return this;
    }

    public TileRendererLayerBuilder context(@NonNull Context context) {
        this.context = context;
        return this;
    }

    private void validate() {
        assert multiMapDataStore != null;
        assert tileCacheName != null;
        assert theme != null;
        assert model != null;
        assert context != null;
    }

    public TileRendererLayer build() {
        validate();
        TileCache tileCache = AndroidUtil.createTileCache(context, tileCacheName,
                model.displayModel.getTileSize(), 1f, 2);
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, multiMapDataStore,
                model.mapViewPosition, hasAlpha, renderLabels, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(theme);
        return tileRendererLayer;
    }
}
