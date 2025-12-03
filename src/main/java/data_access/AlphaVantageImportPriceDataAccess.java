package data_access;

import entity.PriceBar;
import use_case.import_ohlcv.ImportOHLCVPriceDataAccessInterface;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for UC-1 that delegates price fetching to an existing
 * BacktestDataAccessInterface (e.g., AlphaVantageBacktestDataAccessObject).
 */
public class AlphaVantageImportPriceDataAccess implements ImportOHLCVPriceDataAccessInterface {

    private final BacktestDataAccessInterface delegate;

    public AlphaVantageImportPriceDataAccess(BacktestDataAccessInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    public Map<String, List<PriceBar>> fetchPrices(List<String> tickers, LocalDate startDate, LocalDate endDate) {
        return Map.of();
    }

    @Override
    public Map<String, List<PriceBar>> fetchPrices(
            String projectId,
            List<String> tickers,
            LocalDate start,
            LocalDate end
    ) {
        Map<String, List<PriceBar>> result = new HashMap<>();

        for (String ticker : tickers) {
            List<PriceBar> series = delegate.getPriceSeries(ticker, start, end);
            if (series != null && !series.isEmpty()) {
                result.put(ticker, series);
            }
        }

        return result;
    }
}
