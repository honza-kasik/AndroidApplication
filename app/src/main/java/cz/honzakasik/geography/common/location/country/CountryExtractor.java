package cz.honzakasik.geography.common.location.country;

import java.io.IOException;
import java.util.List;

/**
 * This interface sgoul be implemented by any country extractor
 */
public interface CountryExtractor {

    /**
     * Get all extracted countries
     * @return list of extracted countries
     * @throws IOException
     */
    List<Country> getAllCountries() throws IOException;

    /**
     * Registed implementation of ExtractionObserver to observe an extraction
     * @param observer
     */
    void registerObserver(ExtractionObserver observer);

    void removeObserver(ExtractionObserver observer);

    void notifyObserversAboutExtractionProgress(int percentDone);
}
