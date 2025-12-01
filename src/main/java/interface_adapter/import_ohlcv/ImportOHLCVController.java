package interface_adapter.import_ohlcv;

import use_case.import_ohlcv.ImportOHLCVInputBoundary;
import use_case.import_ohlcv.ImportOHLCVInputData;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for UC-1: Import OHLCV.
 * Parses String input from the View into strongly-typed InputData.
 */
public class ImportOHLCVController {

    private final ImportOHLCVInputBoundary interactor;

    public ImportOHLCVController(ImportOHLCVInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Execute the import use case.
     *
     * @param projectId     ID of the project to attach imported prices to.
     * @param tickersCsv    comma-separated ticker symbols (e.g. "AAPL, MSFT, GOOG").
     * @param startDateStr  start date as string (YYYY-MM-DD).
     * @param endDateStr    end date as string (YYYY-MM-DD).
     * @param sourceType    optional hint about the source ("API", "CSV", etc.).
     */
    public void execute(String projectId,
                        String tickersCsv,
                        String startDateStr,
                        String endDateStr,
                        String sourceType) {

        List<String> tickers = parseTickers(tickersCsv);
        LocalDate startDate = parseDateOrNull(startDateStr);
        LocalDate endDate = parseDateOrNull(endDateStr);

        ImportOHLCVInputData inputData = new ImportOHLCVInputData(
                tickers,
                startDate,
                endDate,
                projectId,
                sourceType
        );

        interactor.execute(inputData);
    }

    private List<String> parseTickers(String tickersCsv) {
        List<String> result = new ArrayList<>();
        if (tickersCsv == null) {
            return result;
        }
        String[] parts = tickersCsv.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private LocalDate parseDateOrNull(String s) {
        if (s == null) {
            return null;
        }
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
