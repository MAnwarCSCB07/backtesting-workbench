package use_case.run_backtest;

import java.time.LocalDate;

/**
 * Input data for the Run Backtest use case.
 */
public class RunBacktestInputData {

    private final String projectId;
    private final String ticker;
    private final double initialCapital;
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Risk-free rate in PERCENT per year (e.g., 4.5 means 4.5%).
     */
    private final double riskFreeRatePercent;

    public RunBacktestInputData(String projectId,
                                String ticker,
                                double initialCapital,
                                LocalDate startDate,
                                LocalDate endDate,
                                double riskFreeRatePercent) {
        this.projectId = projectId;
        this.ticker = ticker;
        this.initialCapital = initialCapital;
        this.startDate = startDate;
        this.endDate = endDate;
        this.riskFreeRatePercent = riskFreeRatePercent;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getTicker() {
        return ticker;
    }

    public double getInitialCapital() {
        return initialCapital;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Risk-free rate in percent per year (e.g., 4.5 means 4.5%).
     */
    public double getRiskFreeRatePercent() {
        return riskFreeRatePercent;
    }
}