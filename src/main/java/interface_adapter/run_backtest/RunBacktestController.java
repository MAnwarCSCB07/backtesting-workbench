package interface_adapter.run_backtest;

import use_case.run_backtest.RunBacktestInputBoundary;
import use_case.run_backtest.RunBacktestInputData;

public class RunBacktestController {

    private final RunBacktestInputBoundary interactor;

    public RunBacktestController(RunBacktestInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void runBacktest(String projectId) {
        RunBacktestInputData inputData = new RunBacktestInputData(projectId);
        interactor.execute(inputData);
    }
}