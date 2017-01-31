package cz.honzakasik.geography;

import android.app.Application;
import android.content.Context;
import android.graphics.Picture;
import android.util.LruCache;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import cz.honzakasik.geography.common.location.country.Country;

public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static Context context;

    private List<Country> countries = new LinkedList<>();
    private boolean isSet = false;
    private static LruCache<Country, Picture> globalFlagCache;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidGraphicFactory.createInstance(this);
        globalFlagCache = new LruCache<>(128);
        context = getApplicationContext();
        logger.info("APP IS CREATED");

    }

    public List<Country> getCountries() {
        logger.info("Someone got countries list of size {}, is set? {}!", this.countries.size(), String.valueOf(isSet));
        return countries;
    }

    public void setCountries(List<Country> countries) {
        logger.info("Countries is set to size {}", countries.size());
        this.countries = countries;
        isSet = true;
    }

    public LruCache<Country, Picture> getGlobalFlagCache() {
        return globalFlagCache;
    }

    public static Context getContext() {
        return context;
    }

}
