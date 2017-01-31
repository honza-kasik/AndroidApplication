package cz.honzakasik.geography.common.location.country.geojsonextractor;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import com.neovisionaries.i18n.LanguageCode;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.country.Territory;
import cz.honzakasik.geography.common.location.country.TerritoryPolygon;
import cz.honzakasik.geography.common.utils.PropUtils;

public class GeoJsonExtractorWorker implements Callable<Country> {

    private Logger logger = LoggerFactory.getLogger(GeoJsonExtractorWorker.class);

    private Context context;
    private ObjectMapper objectMapper;
    private String path;

    public GeoJsonExtractorWorker(Context context, ObjectMapper objectMapper, String path) {
        this.context = context;
        this.objectMapper = objectMapper;
        this.path = path;
    }

    @Override
    public Country call() throws Exception {
        JsonNode country = objectMapper.readValue(
                context.getAssets().open(path), JsonNode.class);

        JsonNode feature = country.get(PropUtils.get("extractor.geojson.features")).get(0);
        JsonNode properties = feature.get(PropUtils.get("extractor.geojson.properties"));
        JsonNode geometry = feature.get(PropUtils.get("extractor.geojson.geometry"));

        logger.debug("Parsing '{}'.", country.toString());
        CountryCode countryCode = CountryCode
                .getByCode(properties.get(PropUtils.get("extractor.geojson.properties.alpha2"))
                        .asText());


        Territory.Builder territoryBuilder = new Territory.Builder();

        Country.Builder countryBuilder = new Country.Builder()
                .name(properties.get(PropUtils.get("extractor.geojson.properties.name")).asText())
                .countryCode(countryCode)
                .area(properties.get(PropUtils.get("extractor.geojson.properties.area")).asInt())
                .population(properties.get(PropUtils.get("extractor.geojson.properties.population")).asInt());

        //currencies
        for (JsonNode currency : properties.get(PropUtils.get("extractor.geojson.properties.currency"))) {
            countryBuilder.currency(CurrencyCode.getByCode(currency.asText()));
        }

        //languages
        Iterator<String> languageCodesIterator = properties
                .get(PropUtils.get("extractor.geojson.properties.languages")).fieldNames();
        while (languageCodesIterator.hasNext()) {
            String languageCodeString = languageCodesIterator.next();
            logger.debug("Processing language code '{}' of '{}'.", languageCodeString,
                    countryCode.toString());
            countryBuilder.language(LanguageCode.getByCode(languageCodeString));
        }

        //neighbours
        for (JsonNode neighbour : properties.get(PropUtils.get("extractor.geojson.properties.borders"))) {
            logger.debug("Processing '{}' neighbour code of '{}'.", neighbour.asText(),
                    countryCode.toString());
            countryBuilder.neighbour(CountryCode.getByCode(neighbour.asText()));
        }

        //center

        JsonNode coordinatesTuple = properties.get(PropUtils.get("extractor.geojson.geometry.center"));
        countryBuilder.center(new LatLong(coordinatesTuple.get(0).asDouble(),
                                    coordinatesTuple.get(1).asDouble()));

        //bounding boxes
        for (JsonNode boundingBox : properties.get(PropUtils.get("extractor.geojson.properties.bboxes"))) {
            territoryBuilder.boundingBox(
                    new BoundingBox(boundingBox.get(1).asDouble(),
                            boundingBox.get(0).asDouble(),
                            boundingBox.get(3).asDouble(),
                            boundingBox.get(2).asDouble()));
        }

        List<TerritoryPolygon> polygons = getTerritoryPolygons(geometry);
        for (TerritoryPolygon polygon : polygons) {
            territoryBuilder.polygon(polygon);
        }
        logger.debug("Added {} territory polygons to country builder.", polygons.size());


        countryBuilder.territory(territoryBuilder.build());
        return  countryBuilder.build();
    }

    private List<TerritoryPolygon> getTerritoryPolygons(JsonNode geometry)
            throws IOException {

        List<TerritoryPolygon> polygons = new LinkedList<>();

        logger.info(geometry.toString());
        String type = geometry.get(PropUtils.get("extractor.geojson.geometry.type")).asText();
        GeometryType geometryType = GeometryType.getByJsonName(type);

        JsonNode coordinates = geometry.get(PropUtils.get("extractor.geojson.geometry.coordinates"));

        if (geometryType == GeometryType.MULTIPOLYGON) {
            for (JsonNode polygon : coordinates) {
                polygons.add(getTerritoryPolygon(polygon));
            }
        } else if (geometryType == GeometryType.POLYGON) {
            polygons.add(getTerritoryPolygon(coordinates));
        }

        return polygons;
    }

    private TerritoryPolygon getTerritoryPolygon(JsonNode coordinates) {
        List<LatLong> latLongs = new LinkedList<>();
        for (JsonNode coordinate : coordinates) {
            logger.debug("Adding pair of coordinates {} from JSON", coordinate);
            latLongs.add(new LatLong(coordinate.get(0).asDouble(), coordinate.get(1).asDouble()));
        }
        return new TerritoryPolygon(latLongs);
    }

    private enum GeometryType {

        MULTIPOLYGON("MultiPolygon"), POLYGON("Polygon");

        private String jsonName;

        GeometryType(String jsonName) {
            this.jsonName = jsonName;
        }

        public String getJsonName() {
            return jsonName;
        }

        public static GeometryType getByJsonName(String jsonName) {
            for (GeometryType value : values()) {
                if (value.getJsonName().equals(jsonName))
                    return value;
            }
            return null;
        }
    }
}
