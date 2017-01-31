package cz.honzakasik.geography.common.location.country;

/**
 * Interface implemented by observer of an extraction process
 */
public interface ExtractionObserver {
    /**
     * When the progress of extraction is updated.
     * @param percentDone how much percent of work is done
     */
    void onExtractionProgressUpdate(int percentDone);
}
