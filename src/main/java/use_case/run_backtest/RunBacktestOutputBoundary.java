package use_case.run_backtest;

public interface RunBacktestOutputBoundary {

    void present(RunBacktestOutputData outputData);

    void presentFailure(String errorMessage);
}