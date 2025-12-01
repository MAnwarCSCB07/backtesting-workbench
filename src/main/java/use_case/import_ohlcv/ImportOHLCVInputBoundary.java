package use_case.import_ohlcv;

/**
 * Input boundary for the Import OHLCV use case.
 */
public interface ImportOHLCVInputBoundary {

    /**
     * Execute the import for the given input data.
     *
     * @param inputData parameters for the import (tickers, dates, project, source).
     */
    void execute(ImportOHLCVInputData inputData);
}
