package use_case.import_ohlcv;

import entity.PriceBar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Gateway for fetching OHLCV data from an external source
 * (API, CSV, database, etc.).
 *
 * Concrete implementations live in the data_access layer.
 */
public interface ImportOHLCVPriceDataAccessInterface {

    /**
     * Fetch OHLCV price series for the given tickers and date range.
     *
     * @param tickers   list of ticker symbols.
     * @param startDate inclusive start date.
     * @param endDate   inclusive end date.
     * @return a map from ticker symbol to a list of PriceBar objects.
     *         Tickers that cannot be fetched may be returned with an empty list
     *         or omitted entirely (the interactor will treat them as missing).
     * @throws RuntimeException (or a more specific custom exception)
     *         if the underlying fetch fails (network, file error, etc.).
     */
    Map<String, List<PriceBar>> fetchPrices(List<String> tickers,
                                            LocalDate startDate,
                                            LocalDate endDate);
}
