package use_case.run_backtest;

public class RunBacktestInputData {

    private final String projectId;
    private final String ticker;      // may be null/blank → use default from config
    private final String capital;     // string from UI, parsed in interactor
    private final String startDate;   // "YYYY-MM-DD" or blank
    private final String endDate;     // "YYYY-MM-DD" or blank

    public RunBacktestInputData(String projectId,
                                String ticker,
                                String capital,
                                String startDate,
                                String endDate) {
        this.projectId = projectId;
        this.ticker = ticker;
        this.capital = capital;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getTicker() {
        return ticker;
    }

    public String getCapital() {
        return capital;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}