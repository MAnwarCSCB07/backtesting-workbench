package use_case.run_backtest;

import data_access.BacktestDataAccessInterface;
import entity.BacktestConfig;
import entity.Universe;
import entity.PriceBar;
import entity.BacktestResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for RunBacktestInteractor.
 * Uses a fake DAO and a fake presenter to keep everything in-memory.
 */
public class RunBacktestInteractorTest {

    /**
     * Fake DAO for testing.
     * Returns deterministic OHLCV data + universe + config.
     */
    static class FakeBacktestDAO implements BacktestDataAccessInterface {

        @Override
        public BacktestConfig getConfig(String projectId) {
            return new BacktestConfig(
                    projectId,
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 10),
                    10_000.0,
                    "buy_and_hold"
            );
        }

        @Override
        public Universe getUniverse(String projectId) {
            List<String> tickers = new ArrayList<>();
            tickers.add("AAPL");
            return new Universe(tickers);
        }

        @Override
        public List<PriceBar> getPriceSeries(String ticker,
                                             LocalDate start,
                                             LocalDate end) {
            // 10 days of monotonically increasing prices
            List<PriceBar> bars = new ArrayList<>();
            LocalDate d = start;
            double price = 100.0;
            for (int i = 0; i < 10; i++) {
                double open = price;
                double close = price * 1.01;     // +1% per day
                double high = close;
                double low = open * 0.99;
                long volume = 1_000L;

                bars.add(new PriceBar(
                        "AAPL",
                        d,
                        open,
                        high,
                        low,
                        close,
                        volume
                ));

                price = close;
                d = d.plusDays(1);
            }
            return bars;
        }

        @Override
        public void saveResult(BacktestResult result) {
            // no-op for tests
        }
    }

    /**
     * Fake presenter that just captures success / error output.
     */
    static class FakePresenter implements RunBacktestOutputBoundary {

        RunBacktestOutputData capturedSuccess;
        String capturedError;

        @Override
        public void presentSuccess(RunBacktestOutputData outputData) {
            this.capturedSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.capturedError = errorMessage;
        }
    }

    @Test
    void testSuccessfulBacktest() {
        // Arrange
        FakeBacktestDAO dao = new FakeBacktestDAO();
        FakePresenter presenter = new FakePresenter();
        RunBacktestInteractor interactor = new RunBacktestInteractor(dao, presenter);

        String projectId = "demo-project";
        String ticker = "AAPL";
        double initialCapital = 10_000.0;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 10);
        double riskFreeRate = 0.02; // 2% per year

        RunBacktestInputData inputData = new RunBacktestInputData(
                projectId,
                ticker,
                initialCapital,
                start,
                end,
                riskFreeRate
        );

        // Act
        interactor.execute(inputData);

        // Assert: no error and success captured
        assertNull(presenter.capturedError);
        assertNotNull(presenter.capturedSuccess);

        RunBacktestOutputData out = presenter.capturedSuccess;

        // Final value should be > initial capital (prices went up)
        assertTrue(out.getFinalValue() > initialCapital);

        // Total and annualized return should be non-zero
        assertTrue(out.getTotalReturn() > 0);
        assertTrue(out.getAnnualizedReturn() > 0);

        // Volatility is non-negative
        assertTrue(out.getVolatilityAnnualized() >= 0);

        // Sharpe can be any sign but should be finite
        assertFalse(Double.isNaN(out.getSharpeRatio()));
        assertFalse(Double.isInfinite(out.getSharpeRatio()));

        // Worst daily loss should be <= 0 (a loss or 0)
        assertTrue(out.getWorstDailyLossPercent() <= 0);

        // Equity curve should exist and have at least 2 points
        assertNotNull(out.getEquityCurve());
        assertFalse(out.getEquityCurve().isEmpty());
        assertTrue(out.getEquityCurve().size() >= 2);
    }

}