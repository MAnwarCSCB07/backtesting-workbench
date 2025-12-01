package use_case.run_backtest;

import data_access.BacktestDataAccessInterface;
import entity.BacktestConfig;
import entity.BacktestResult;
import entity.PriceBar;
import entity.Universe;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

        // ----- parse overrides from input -----
        String tickerOverride = safeTrim(inputData.getTicker());
        String capitalStr     = safeTrim(inputData.getCapital());
        String startStr       = safeTrim(inputData.getStartDate());
        String endStr         = safeTrim(inputData.getEndDate());

        Double capitalOverride = null;
        if (capitalStr != null && !capitalStr.isEmpty()) {
            try {
                capitalOverride = Double.parseDouble(capitalStr);
                if (capitalOverride <= 0) {
                    presenter.presentFailure("Initial capital must be positive.");
                    return;
                }
            } catch (NumberFormatException e) {
                presenter.presentFailure("Invalid number for initial capital.");
                return;
            }
        }

        LocalDate startOverride = null;
        LocalDate endOverride   = null;

        try {
            if (startStr != null && !startStr.isEmpty()) {
                startOverride = LocalDate.parse(startStr);
            }
            if (endStr != null && !endStr.isEmpty()) {
                endOverride = LocalDate.parse(endStr);
            }
        } catch (DateTimeParseException e) {
            presenter.presentFailure("Dates must be in format YYYY-MM-DD.");
            return;
        }

        // ----- pick effective parameters (override or default) -----
        double initialCapital =
                capitalOverride != null ? capitalOverride : config.getInitialCapital();

        LocalDate start = startOverride != null ? startOverride : config.getStartDate();
        LocalDate end   = endOverride   != null ? endOverride   : config.getEndDate();

        // 100-day limit (inclusive)
        if (start != null && end != null && end.isAfter(start.plusDays(100))) {
            presenter.presentFailure("Free Alpha Vantage only supports about 100 days; " +
                    "please choose a shorter date range.");
            return;
        }

        // decide ticker
        String ticker;
        if (tickerOverride != null && !tickerOverride.isEmpty()) {
            ticker = tickerOverride.toUpperCase();
        } else {
            if (universe == null || universe.getTickers().isEmpty()) {
                presenter.presentFailure("No universe defined for project: " + projectId);
                return;
            }
            ticker = universe.getTickers().get(0);
        }

        // ----- fetch prices -----
        List<PriceBar> prices = backtestDAO.getPriceSeries(ticker, start, end);
        if (prices.isEmpty()) {
            presenter.presentFailure("No price data found for ticker: " + ticker);
            return;
        }

        // ----- simple buy & hold backtest -----
        List<Double> equityCurve = new ArrayList<>();

        double firstClose = prices.get(0).getClose();
        double shares = initialCapital / firstClose;

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
        double totalReturn = (finalValue - initialCapital) / initialCapital * 100.0;

        BacktestResult result = new BacktestResult(
                projectId,
                finalValue,
                maxDrawdown,
                equityCurve
        );
        backtestDAO.saveResult(result);

        RunBacktestOutputData outputData =
                new RunBacktestOutputData(finalValue, maxDrawdown, totalReturn);

        presenter.presentSuccess(outputData);
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }
}