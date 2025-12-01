package use_case.run_backtest;

public class RunBacktestInputData {

    private final String projectId;

    public RunBacktestInputData(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }
}