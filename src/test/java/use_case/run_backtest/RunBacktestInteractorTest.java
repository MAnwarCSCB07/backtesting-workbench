package use_case.run_backtest;

import data_access.BacktestDataAccessInterface;
import entity.BacktestResult;
import entity.PriceBar;
import entity.BacktestConfig;
import entity.Universe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

class RunBacktestInteractorTest {

    /**
     * Fake DAO used only for the test.
     */
    private static class FakeDAO implements BacktestDataAccessInterface {

        @Override
        public BacktestConfig getConfig(String projectId) {
            return new BacktestConfig(
                    projectId,
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 10),
                    10000.0,
                    "buy_and_hold"
            );
        }

        @Override
        public Universe getUniverse(String projectId) {
            return new Universe(List.of("AAPL"));
        }

        @Override
        public List<PriceBar> getPriceSeries(String ticker, LocalDate start, LocalDate end) {
            List<PriceBar> list = new ArrayList<>();
            // deterministic prices
            list.add(new PriceBar("AAPL", LocalDate.of(2020, 1, 1), 100, 100, 100, 100, 1000));
            list.add(new PriceBar("AAPL", LocalDate.of(2020, 1, 2), 110, 110, 110, 110, 1000));
            return list;
        }

        @Override
        public void saveResult(BacktestResult result) {
            // do nothing
        }
    }

    /**
     * Fake Presenter captures output for assertions.
     */
    private static class FakePresenter implements RunBacktestOutputBoundary {

        RunBacktestOutputData captured;
        String failureMessage;

        @Override
        public void presentSuccess(RunBacktestOutputData outputData) {
            this.captured = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.failureMessage = errorMessage;
        }
    }

    @Test
    void testSuccessfulBacktest() {
        FakeDAO dao = new FakeDAO();
        FakePresenter presenter = new FakePresenter();

        RunBacktestInteractor interactor = new RunBacktestInteractor(dao, presenter);

        RunBacktestInputData input = new RunBacktestInputData(
                "demo",
                "AAPL",
                "10000",
                "2020-01-01",
                "2020-01-10"
        );

        interactor.execute(input);

        Assertions.assertNull(presenter.failureMessage);
        Assertions.assertNotNull(presenter.captured);

        // final value should grow by 10% (100 → 110)
        Assertions.assertEquals(11000.0, presenter.captured.getFinalValue(), 0.01);
    }
}