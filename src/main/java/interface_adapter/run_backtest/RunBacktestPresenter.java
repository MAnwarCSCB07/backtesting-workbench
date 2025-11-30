package interface_adapter.run_backtest;

import use_case.run_backtest.RunBacktestOutputBoundary;
import use_case.run_backtest.RunBacktestOutputData;

public class RunBacktestPresenter implements RunBacktestOutputBoundary {

    private final RunBacktestViewModel viewModel;

    public RunBacktestPresenter(RunBacktestViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(RunBacktestOutputData outputData) {
        RunBacktestState state = viewModel.getState();

        state.setProjectId(outputData.getProjectId());
        state.setFinalValue(outputData.getFinalValue());
        state.setMaxDrawdown(outputData.getMaxDrawdown());
        state.setEquityCurve(outputData.getEquityCurve());
        state.setStatusMessage("Backtest completed successfully.");

        viewModel.setState(state);
        viewModel.fireStateChanged();
    }

    @Override
    public void presentFailure(String errorMessage) {
        RunBacktestState state = viewModel.getState();

        state.setStatusMessage(errorMessage);
        state.setEquityCurve(null);
        state.setFinalValue(null);
        state.setMaxDrawdown(null);

        viewModel.setState(state);
        viewModel.fireStateChanged();
    }
}
