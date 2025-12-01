package interface_adapter.run_backtest;

import use_case.run_backtest.RunBacktestOutputBoundary;
import use_case.run_backtest.RunBacktestOutputData;

public class RunBacktestPresenter implements RunBacktestOutputBoundary {

    private final RunBacktestViewModel viewModel;

    public RunBacktestPresenter(RunBacktestViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(RunBacktestOutputData outputData) {

        RunBacktestState state = viewModel.getState();

        state.setFinalValue(outputData.getFinalValue());
        state.setMaxDrawdown(outputData.getMaxDrawdown());
        state.setTotalReturn(outputData.getTotalReturn());
        state.setAnnualizedReturn(outputData.getAnnualizedReturn());
        state.setVolatility(outputData.getVolatilityAnnualized());
        state.setSharpeRatio(outputData.getSharpeRatio());
        state.setWorstDailyLoss(outputData.getWorstDailyLossPercent());
        state.setEquityCurve(outputData.getEquityCurve());

        state.setStatusMessage("Backtest completed successfully.");

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void presentFailure(String errorMessage) {
        RunBacktestState state = viewModel.getState();

        state.setStatusMessage(errorMessage);
        state.setEquityCurve(null);
        state.setFinalValue(null);
        state.setMaxDrawdown(null);

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
