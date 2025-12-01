package interface_adapter.import_ohlcv;

import use_case.import_ohlcv.ImportOHLCVInputBoundary;
import use_case.import_ohlcv.ImportOHLCVInputData;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for UC-1: Import OHLCV.
 *
 * Translates raw Strings from the InputStockDataView into
 * a type-safe ImportOHLCVInputData and calls the interactor.
 */
public class ImportOHLCVController {

    private final ImportOHLCVInputBoundary interactor;

    public ImportOHLCVController(ImportOHLCVInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Main entry point used by InputStockDataView.
     *
     * This signature is intentionally very tolerant:
     *  - projectId     : "demo-project"
     *  - tickersCsv    : "AAPL,MSFT,GOOG"
     *  - startDateStr  : "2025-01-01"
     *  - endDateStr    : "2025-03-01"
     *  - source        : "Alpha Vantage API" or "CSV", etc. (optional for now)
     */
    public void importPrices(String projectId,
                             String tickersCsv,
                             String startDateStr,
                             String endDateStr,
                             String source) {

        List<String> tickers = parseTickersCsv(tickersCsv);
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDateStr);
            end   = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException e) {

            start = null;
            end   = null;
        }

        ImportOHLCVInputData inputData = new ImportOHLCVInputData(
                tickers,
                start,
                end,
                projectId,
                source
        );



        interactor.execute(inputData);
    }

    /**
     * Overload with 4 parameters in case your view calls it without source.
     * It just defaults source to "API".
     */
    public void importPrices(String projectId,
                             String tickersCsv,
                             String startDateStr,
                             String endDateStr) {
        importPrices(projectId, tickersCsv, startDateStr, endDateStr, "API");
    }

    // ---- helpers ----

    private List<String> parseTickersCsv(String tickersCsv) {
        if (tickersCsv == null || tickersCsv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tickersCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private LocalDate parseDateOrNull(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            // For now, if parsing fails, we just return null; the Interactor can treat null as invalid.
            return null;
        }
    }
}
