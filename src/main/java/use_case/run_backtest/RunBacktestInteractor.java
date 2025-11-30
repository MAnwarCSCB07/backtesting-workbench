package use_case.run_backtest;

import data_access.BacktestDataAccessInterface;
import entity.BacktestConfig;
import entity.Universe;
import entity.PriceBar;
import entity.BacktestResult;

import java.util.ArrayList;
import java.util.List;

public class RunBacktestInteractor implements RunBacktestInputBoundary {

    private final BacktestDataAccessInterface backtestDAO;
    private final RunBacktestOutputBoundary presenter;

    public RunBacktestInteractor(BacktestDataAccessInterface backtestDAO,
                                 RunBacktestOutputBoundary presenter) {
        this.backtestDAO = backtestDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(RunBacktestInputData inputData) {
        String projectId = inputData.getProjectId();

        BacktestConfig config = backtestDAO.getConfig(projectId);
        if (config == null) {
            presenter.presentFailure("No backtest configuration found for project: " + projectId);
            return;
        }

        Universe universe = backtestDAO.getUniverse(projectId);
        if (universe == null || universe.getTickers().isEmpty()) {
            presenter.presentFailure("Universe is empty for project: " + projectId);
            return;
        }

        // For now, keep it simple: use the first ticker only.
        String ticker = universe.getTickers().get(0);

        List<PriceBar> prices = backtestDAO.getPriceSeries(
                ticker,
                config.getStartDate(),
                config.getEndDate()
        );

        if (prices == null || prices.isEmpty()) {
            presenter.presentFailure("No price data found for ticker: " + ticker);
            return;
        }

        double initialCapital = config.getInitialCapital();
        double firstClose = prices.get(0).getClose();
        double shares = initialCapital / firstClose;

        List<Double> equityCurve = new ArrayList<>();
        double peak = initialCapital;
        double maxDrawdown = 0.0;

        for (PriceBar bar : prices) {
            double value = shares * bar.getClose();
            equityCurve.add(value);

            if (value > peak) {
                peak = value;
            }
            double drawdown = (peak - value) / peak;
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }

        double finalValue = equityCurve.get(equityCurve.size() - 1);

        BacktestResult result = new BacktestResult(projectId, finalValue, maxDrawdown, equityCurve);
        backtestDAO.saveResult(result);

        RunBacktestOutputData outputData =
                new RunBacktestOutputData(projectId, finalValue, maxDrawdown, equityCurve);

        presenter.present(outputData);
    }
}