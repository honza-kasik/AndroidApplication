package cz.honzakasik.geography.common.location;

import android.content.Context;

import org.mapsforge.core.model.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.country.CountryExtractor;

/**
 * Class providing methods for reverse geolocation
 */
public class LocationManager {

    private Logger logger = LoggerFactory.getLogger(LocationManager.class);

    private CountryExtractor countryExtractor;
    private Context context;

    public LocationManager(CountryExtractor countryExtractor) {
        this.countryExtractor = countryExtractor;
    }

    public LocationManager(Context context) {
        this.context = context;
    }

    public Country inWhichCountryPointIs(LatLong coordinates) throws PointNotInAnyCountryException {
        return inWhichCountryPointIs(coordinates.latitude, coordinates.longitude);
    }

    public Country inWhichCountryPointIs(double latitude, double longitude)
            throws PointNotInAnyCountryException {
        List<Country> countries = null;
        try {
            if (countryExtractor != null) {
                countries = countryExtractor.getAllCountries();
            } else if (context != null) {
                countries = ((App)context.getApplicationContext()).getCountries();
            }
        } catch (IOException e) {
            logger.error("Could not load countries, {}", e.toString());
        }

        List<Country> candidateCountries = new LinkedList<>();
        assert countries != null;

        for (Country country : countries) {
            if (country.isPointInBoundingBox(longitude, latitude)) {
                candidateCountries.add(country);
            }
        }

        logger.info("Possible clicked countries are {}", candidateCountries.toString());

        if (candidateCountries.size() == 0) {
            throw new PointNotInAnyCountryException("No known country which contains this point" +
                    " were found by bounding box method.");
        } else if (candidateCountries.size() == 1) {
            return candidateCountries.get(0);
        } else {
            for (Country country : candidateCountries) {
                if (country.contains(longitude, latitude)) {
                    return country;
                }
            }
        }
        throw new PointNotInAnyCountryException("Country which contains point was not found");
    }
}
