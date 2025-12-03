package interface_adapter.run_backtest;

import use_case.run_backtest.RunBacktestInputBoundary;
import use_case.run_backtest.RunBacktestInputData;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Controller for the Run Backtest use case.
 * Parses Strings from the View and builds an InputData object.
 */
public class RunBacktestController {

    private final RunBacktestInputBoundary interactor;

    public RunBacktestController(RunBacktestInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * @param projectIdStr  project id (e.g. "demo-project")
     * @param tickerStr     symbol (e.g. "AAPL")
     * @param capitalStr    initial capital as String
     * @param startStr      ISO date string or empty
     * @param endStr        ISO date string or empty
     * @param riskFreeStr   risk-free rate in percent per year, e.g. "4.5" (empty means 0)
     */
    public void runBacktest(String projectIdStr,
                            String tickerStr,
                            String capitalStr,
                            String startStr,
                            String endStr,
                            String riskFreeStr) {

        String projectId = projectIdStr == null ? "" : projectIdStr.trim();
        String ticker = tickerStr == null ? "" : tickerStr.trim();

        double initialCapital;
        try {
            initialCapital = Double.parseDouble(capitalStr.trim());
        } catch (NumberFormatException e) {
            // If parsing fails, just send a negative number to signal "invalid" to the interactor
            initialCapital = -1.0;
        }

        LocalDate startDate = parseDateOrNull(startStr);
        LocalDate endDate = parseDateOrNull(endStr);

        double riskFreePercent;
        try {
            riskFreePercent = riskFreeStr == null || riskFreeStr.trim().isEmpty()
                    ? 0.0
                    : Double.parseDouble(riskFreeStr.trim());
        } catch (NumberFormatException e) {
            riskFreePercent = 0.0;
        }

        RunBacktestInputData inputData = new RunBacktestInputData(
                projectId,
                ticker,
                initialCapital,
                startDate,
                endDate,
                riskFreePercent
        );

        interactor.execute(inputData);
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