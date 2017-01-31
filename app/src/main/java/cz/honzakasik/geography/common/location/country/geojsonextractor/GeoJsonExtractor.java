package cz.honzakasik.geography.common.location.country.geojsonextractor;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.country.CountryExtractor;
import cz.honzakasik.geography.common.location.country.ExtractionObserver;
import cz.honzakasik.geography.common.utils.PropUtils;

public class GeoJsonExtractor implements CountryExtractor {

    private Logger logger = LoggerFactory.getLogger(GeoJsonExtractor.class);

    private Context context;
    private ObjectMapper objectMapper;
    private List<Country> countries;

    private List<ExtractionObserver> observers;

    public GeoJsonExtractor(Context context) {
        this.context = context;
        objectMapper = new ObjectMapper();
    }

    @Override
    public List<Country> getAllCountries() throws IOException {
        if (countries != null) {
            return countries;
        }


        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<List<Country>> countriesFuture = executor.submit(new MainWorker());

        executor.shutdown();

        try {
            this.countries = countriesFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return countries;
    }

    private class MainWorker implements Callable<List<Country>> {

        @Override
        public List<Country> call() throws Exception {
            String folder = PropUtils.get("extractor.geojson.source.path");

            List<Country> countries = new LinkedList<>();
            String[] files = context.getAssets().list(folder);
            int totalAmount = files.length;
            logger.info("Total amount of files to extract is {}", totalAmount);

            final StopWatch stopwatch = new StopWatch();
            stopwatch.start();

            int threads = Runtime.getRuntime().availableProcessors();
            ExecutorService service = Executors.newFixedThreadPool(threads);

            List<Future<Country>> futures = new LinkedList<>();
            for (String file : files) {

                String path = folder + File.separator + file;

                logger.debug("Submitting parsing of '{}' to executor service.", path);
                futures.add(
                        service.submit(
                                new GeoJsonExtractorWorker(context, objectMapper, path)
                        )
                );
            }

            service.shutdown();

            int amountDone = 0;

            for (Future<Country> future : futures) {
                try {
                    Country country = future.get();
                    countries.add(country);
                    logger.info(country.getName());
                    logger.info(country.getTerritory().getBoundingBoxes().toString());
                    notifyObserversAboutExtractionProgress((int) Math.floor(amountDone++ / (totalAmount/100.0)));
                }  catch (InterruptedException | ExecutionException e) {
                    logger.error("Failed to execute multithreaded extraction!", e.getCause());
                }
            }

            logger.info("Parsing completed at {}.", stopwatch);
            return countries;
        }
    }

    @Override
    public void registerObserver(ExtractionObserver observer) {
        if (this.observers == null) {
            this.observers = new LinkedList<>();
        }
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(ExtractionObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObserversAboutExtractionProgress(int percentDone) {
        if (this.observers == null) {
            return;//TODO handle this state better
        }
        for (ExtractionObserver observer : this.observers) {
            observer.onExtractionProgressUpdate(percentDone);
        }
        logger.info("Notified observers about {}% is done", percentDone);
    }
}
