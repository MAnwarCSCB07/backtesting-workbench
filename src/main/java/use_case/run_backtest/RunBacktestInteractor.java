package use_case.run_backtest;

import data_access.BacktestDataAccessInterface;
import entity.BacktestConfig;
import entity.BacktestResult;
import entity.PriceBar;
import entity.Universe;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for running a simple buy-and-hold backtest.
 * Now uses a user-provided risk-free rate when computing Sharpe.
 */
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
            presenter.presentFailure("No backtest config found for project: " + projectId);
            return;
        }

        // Determine dates (user input overrides config if provided)
        LocalDate start = inputData.getStartDate() != null ? inputData.getStartDate() : config.getStartDate();
        LocalDate end = inputData.getEndDate() != null ? inputData.getEndDate() : config.getEndDate();

        if (start == null || end == null) {
            presenter.presentFailure("Start and end dates must be specified.");
            return;
        }

        if (end.isBefore(start)) {
            presenter.presentFailure("End date must be on or after start date.");
            return;
        }

        long days = ChronoUnit.DAYS.between(start, end);
        if (days > 100) {
            presenter.presentFailure("Date range is too long. Alpha Vantage free tier supports about 100 days.");
            return;
        }

        // Determine initial capital (user value overrides config if valid)
        double initialCapital = inputData.getInitialCapital() > 0
                ? inputData.getInitialCapital()
                : config.getInitialCapital();

        if (initialCapital <= 0) {
            presenter.presentFailure("Initial capital must be positive.");
            return;
        }

        // Universe + ticker
        Universe universe = backtestDAO.getUniverse(projectId);
        if (universe == null || universe.getTickers().isEmpty()) {
            presenter.presentFailure("Universe has no tickers configured.");
            return;
        }

        String ticker = inputData.getTicker();
        if (ticker == null || ticker.isBlank()) {
            ticker = universe.getTickers().get(0);
        }

        // Fetch OHLCV data
        List<PriceBar> series = backtestDAO.getPriceSeries(ticker, start, end);
        if (series.isEmpty()) {
            presenter.presentFailure("No price data found for ticker: " + ticker);
            return;
        }

        // Simple buy-and-hold: buy at first close, hold to end.
        double firstClose = series.get(0).getClose();
        if (firstClose <= 0) {
            presenter.presentFailure("Invalid first close price for ticker: " + ticker);
            return;
        }

        double shares = initialCapital / firstClose;

        List<Double> equityCurve = new ArrayList<>();
        List<Double> dailyReturns = new ArrayList<>();

        double previousEquity = initialCapital;

        for (PriceBar bar : series) {
            double equity = shares * bar.getClose();
            equityCurve.add(equity);

            if (!dailyReturns.isEmpty() || equityCurve.size() > 1) {
                double r = (equity - previousEquity) / previousEquity; // decimal daily return
                dailyReturns.add(r);
            }

            previousEquity = equity;
        }

        double finalValue = equityCurve.get(equityCurve.size() - 1);
        double totalReturn = (finalValue / initialCapital - 1.0) * 100.0;

        // Max drawdown from equity curve
        double maxDrawdown = computeMaxDrawdown(equityCurve);

        // Use geometric annualization: (final / initial)^(252 / nDays) - 1
        double annualizedReturn;
        if (dailyReturns.isEmpty()) {
            annualizedReturn = 0.0;
        } else {
            double tradingDays = dailyReturns.size();
            annualizedReturn = (Math.pow(finalValue / initialCapital, 252.0 / tradingDays) - 1.0) * 100.0;
        }

        // Volatility (annualized) from daily returns
        double volatilityAnnualized = computeAnnualizedVolatility(dailyReturns);

        // Worst daily loss (most negative daily return)
        double worstDailyLossPercent = computeWorstDailyLoss(dailyReturns) * 100.0;

        // Risk-free adjustment: user enters percent per year (e.g., 4.5)
        double riskFreePercent = inputData.getRiskFreeRatePercent();
        double excessReturn = annualizedReturn - riskFreePercent;
        double sharpeRatio = volatilityAnnualized == 0.0 ? 0.0 : excessReturn / volatilityAnnualized;

        // Persist result
        BacktestResult result = new BacktestResult(
                projectId,
                finalValue,
                maxDrawdown,
                equityCurve
        );
        backtestDAO.saveResult(result);

        // Build output object (includes the new Sharpe definition)
        RunBacktestOutputData outputData = new RunBacktestOutputData(
                finalValue,
                maxDrawdown,
                totalReturn,
                annualizedReturn,
                volatilityAnnualized,
                sharpeRatio,
                worstDailyLossPercent,
                equityCurve          // <-- add this line
        );

        presenter.presentSuccess(outputData);
    }

    private double computeMaxDrawdown(List<Double> equityCurve) {
        double peak = equityCurve.get(0);
        double maxDrawdown = 0.0;

        for (double equity : equityCurve) {
            if (equity > peak) {
                peak = equity;
            }
            double drawdown = (peak - equity) / peak;
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }
        return maxDrawdown;
    }

    private double computeAnnualizedVolatility(List<Double> dailyReturns) {
        if (dailyReturns.isEmpty()) {
            return 0.0;
        }

        double mean = 0.0;
        for (double r : dailyReturns) {
            mean += r;
        }
        mean /= dailyReturns.size();

        double sumSq = 0.0;
        for (double r : dailyReturns) {
            double diff = r - mean;
            sumSq += diff * diff;
        }

        double variance = dailyReturns.size() > 1
                ? sumSq / (dailyReturns.size() - 1)
                : 0.0;

        double dailyVol = Math.sqrt(variance);
        // annualize (assuming ~252 trading days) and convert to percent
        return dailyVol * Math.sqrt(252.0) * 100.0;
    }

    private double computeWorstDailyLoss(List<Double> dailyReturns) {
        double worst = 0.0;
        for (double r : dailyReturns) {
            if (r < worst) {
                worst = r;
            }
        }
        return worst;
    }
}