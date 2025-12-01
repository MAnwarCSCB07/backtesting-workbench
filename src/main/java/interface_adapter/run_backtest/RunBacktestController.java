package interface_adapter.run_backtest;

import use_case.run_backtest.RunBacktestInputBoundary;
import use_case.run_backtest.RunBacktestInputData;

public class RunBacktestController {

    private final RunBacktestInputBoundary interactor;

    public RunBacktestController(RunBacktestInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Trigger the backtest with all user inputs from the view.
     */
    public void runBacktest(String projectId,
                            String ticker,
                            String capital,
                            String startDate,
                            String endDate) {

        RunBacktestInputData inputData =
                new RunBacktestInputData(projectId, ticker, capital, startDate, endDate);

        interactor.execute(inputData);
    }
}