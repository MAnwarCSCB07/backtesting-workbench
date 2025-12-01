package use_case.import_ohlcv;

import entity.PriceBar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interactor for UC-1: Import OHLCV.
 *
 * Responsibilities:
 *  - Validate input (tickers, dates, project).
 *  - Fetch OHLCV data from ImportOHLCVPriceDataAccessInterface.
 *  - Delegate persistence to ImportOHLCVProjectDataAccessInterface.
 *  - Build ImportOHLCVOutputData and call the output boundary.
 */
public class ImportOHLCVInteractor implements ImportOHLCVInputBoundary {

    private final ImportOHLCVPriceDataAccessInterface priceDataAccess;
    private final ImportOHLCVProjectDataAccessInterface projectDataAccess;
    private final ImportOHLCVOutputBoundary outputBoundary;

    public ImportOHLCVInteractor(ImportOHLCVPriceDataAccessInterface priceDataAccess,
                                 ImportOHLCVProjectDataAccessInterface projectDataAccess,
                                 ImportOHLCVOutputBoundary outputBoundary) {
        this.priceDataAccess = priceDataAccess;
        this.projectDataAccess = projectDataAccess;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(ImportOHLCVInputData inputData) {

        // 1. Basic validation
        List<String> tickers = inputData.getTickers();
        LocalDate start = inputData.getStartDate();
        LocalDate end = inputData.getEndDate();
        String projectId = inputData.getProjectId();
        String sourceType = inputData.getSourceType();

        if (projectId == null || projectId.isBlank()) {
            outputBoundary.prepareFailView("Project ID must not be empty.");
            return;
        }

        if (tickers == null || tickers.isEmpty()) {
            outputBoundary.prepareFailView("Please provide at least one ticker symbol.");
            return;
        }

        if (start == null || end == null) {
            outputBoundary.prepareFailView("Start date and end date must both be provided.");
            return;
        }

        if (end.isBefore(start)) {
            outputBoundary.prepareFailView("End date cannot be before start date.");
            return;
        }

        if (!projectDataAccess.existsById(projectId)) {
            outputBoundary.prepareFailView("Project with ID '" + projectId + "' does not exist.");
            return;
        }

        // 2. Fetch prices
        final Map<String, List<PriceBar>> priceSeries;
        try {
            priceSeries = priceDataAccess.fetchPrices(tickers, start, end);
        } catch (RuntimeException e) {
            // Map any low-level issues (network, file, parse) to a user-friendly message.
            outputBoundary.prepareFailView(
                    "Failed to fetch price data: " + safeMessage(e.getMessage())
            );
            return;
        }

        // 3. Determine which tickers were actually loaded and which are missing
        List<String> loadedTickers = new ArrayList<>();
        List<String> missingTickers = new ArrayList<>();

        Set<String> requestedSet = new HashSet<>();
        for (String t : tickers) {
            if (t != null) {
                requestedSet.add(t.trim());
            }
        }

        for (String ticker : requestedSet) {
            List<PriceBar> bars = priceSeries.get(ticker);
            if (bars != null && !bars.isEmpty()) {
                loadedTickers.add(ticker);
            } else {
                missingTickers.add(ticker);
            }
        }

        if (loadedTickers.isEmpty()) {
            outputBoundary.prepareFailView(
                    "No price data was loaded for the requested tickers."
            );
            return;
        }

        // 4. Persist into project
        try {
            projectDataAccess.saveImportedPrices(projectId, priceSeries);
        } catch (RuntimeException e) {
            outputBoundary.prepareFailView(
                    "Failed to save imported prices into project: " + safeMessage(e.getMessage())
            );
            return;
        }

        // 5. Build success output and notify presenter
        ImportOHLCVOutputData outputData = new ImportOHLCVOutputData(
                loadedTickers,
                missingTickers,
                start,
                end,
                projectId,
                sourceType
        );
        outputBoundary.prepareSuccessView(outputData);
    }

    /**
     * Avoids showing "null" messages to users.
     */
    private String safeMessage(String msg) {
        return (msg == null || msg.isBlank()) ? "Unknown error." : msg;
    }
}
