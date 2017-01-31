package cz.honzakasik.geography.common.location.country;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import com.neovisionaries.i18n.LanguageCode;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mapsforge.core.model.LatLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Basic country abstraction class.
 */
public class Country {

    private final Logger logger = LoggerFactory.getLogger(Class.class);

    private final Territory territory;
    private final List<CountryCode> neighbours;
    private final List<CurrencyCode> currencies;
    private final List<LanguageCode> languages;
    private final String name;
    private final CountryCode countryCode;
    private final Integer population;
    private final Integer area;
    private final LatLong center;

    private Country(Builder builder) {
        this.territory = builder.territory;
        this.neighbours = builder.neighbours;
        this.currencies = builder.currencies;
        this.languages = builder.languages;
        this.name = builder.name;
        this.countryCode = builder.countryCode;
        this.population = builder.population;
        this.area = builder.area;
        this.center = builder.center;
    }

    public boolean isPointInBoundingBox(double longitude, double latitude) {
        return territory.isPointInBoundingBox(longitude, latitude);
    }

    public boolean contains(double longitude, double latitude) {
        return territory.contains(latitude, longitude);
    }

    public boolean contains(LatLong coordinates) {
        return contains(coordinates.longitude, coordinates.latitude);
    }

    public Territory getTerritory() {
        return territory;
    }

    public String getName() {
        return name;
    }

    public String getIso2() {
        return countryCode.getAlpha2();
    }

    public String getIso3() {
        return countryCode.getAlpha3();
    }

    public Integer getPopulation() {
        return population;
    }

    public Integer getArea() {
        return area;
    }

    public List<CountryCode> getNeighbours() {
        return neighbours;
    }

    public List<CurrencyCode> getCurrencies() {
        return currencies;
    }

    public List<LanguageCode> getLanguages() {
        return languages;
    }

    public LatLong getCenter() {
        return center;
    }

    public static class Builder {

        private Territory territory;
        private List<CountryCode> neighbours;
        private List<CurrencyCode> currencies;
        private List<LanguageCode> languages;
        private String name;
        private CountryCode countryCode;
        private Integer population;
        private Integer area;
        private LatLong center;

        public Builder () {}

        public Builder name(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name cannot be null!");
            }
            this.name = name;
            return this;
        }

        public Builder territory(Territory territory) {
            if (territory == null) {
                throw new IllegalArgumentException("Territory cannot be null!");
            }
            this.territory = territory;
            return this;
        }

        public Builder population(Integer population) {
            if (population == null) {
                throw new IllegalArgumentException("Population cannot be null!");
            }
            this.population = population;
            return this;
        }

        public Builder area(Integer area) {
            if (area == null) {
                throw new IllegalArgumentException("Area cannot be null!");
            }
            this.area = area;
            return this;
        }

        /**
         * Adds neighbour of this country - state which has common borders with
         * @param neighbour ISO 3166-1 alpha-3 country code (i.e. 'CZE')
         */
        public Builder neighbour(CountryCode neighbour) {
            if (neighbour == null) {
                throw new IllegalArgumentException("Neighbour country code cannot be null!");
            }
            if (this.neighbours == null) {
                this.neighbours = new LinkedList<>();
            }
            this.neighbours.add(neighbour);
            return this;
        }

        /**
         * Adds currency, expected currency in ISO 4217 format
         * @param currency ISO 4217 currency format (i.e. 'CZK')
         */
        public Builder currency(CurrencyCode currency) {
            if (currency == null) {
                throw new IllegalArgumentException("Currency code cannot be null!");
            }
            if (this.currencies == null) {
                this.currencies = new LinkedList<>();
            }
            this.currencies.add(currency);
            return this;
        }

        public Builder language(LanguageCode language) {
            if (language == null) {
                throw new IllegalArgumentException("Language code cannot be null!");
            }
            if (this.languages == null) {
                this.languages = new LinkedList<>();
            }
            this.languages.add(language);
            return this;
        }

        public Builder countryCode(CountryCode countryCode) {
            if (countryCode == null) {
                throw new IllegalArgumentException("Country code cannot be null!");
            }
            this.countryCode = countryCode;
            return this;
        }

        public Builder center(LatLong center) {
            this.center = center;
            return this;
        }

        public Country build() {
            return new Country(this);
        }

    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", iso2='" + countryCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        return new EqualsBuilder()
                .append(logger, country.logger)
                .append(territory, country.territory)
                .append(neighbours, country.neighbours)
                .append(currencies, country.currencies)
                .append(languages, country.languages)
                .append(name, country.name)
                .append(countryCode, country.countryCode)
                .append(population, country.population)
                .append(area, country.area)
                .append(center, country.center)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(logger)
                .append(territory)
                .append(neighbours)
                .append(currencies)
                .append(languages)
                .append(name)
                .append(countryCode)
                .append(population)
                .append(area)
                .append(center)
                .toHashCode();
    }
}
