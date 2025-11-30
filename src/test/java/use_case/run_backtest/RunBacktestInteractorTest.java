package use_case.run_backtest;

import data_access.BacktestDataAccessInterface;
import entity.BacktestConfig;
import entity.Universe;
import entity.PriceBar;
import entity.BacktestResult;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RunBacktestInteractorTest {

    // Fake DAO for predictable testing
    private static class FakeDAO implements BacktestDataAccessInterface {

        @Override
        public BacktestConfig getConfig(String projectId) {
            return new BacktestConfig(
                    projectId,
                    LocalDate.of(2020, 1, 1),
                    LocalDate.of(2020, 1, 3),
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
            List<PriceBar> prices = new ArrayList<>();
            prices.add(new PriceBar("AAPL", LocalDate.of(2020, 1, 1),
                    100, 101, 99, 100, 1000000));
            prices.add(new PriceBar("AAPL", LocalDate.of(2020, 1, 2),
                    101, 102, 100, 101, 1000000));
            prices.add(new PriceBar("AAPL", LocalDate.of(2020, 1, 3),
                    102, 103, 101, 102, 1000000));
            return prices;
        }

        @Override
        public void saveResult(BacktestResult result) {
            // Not needed for this test
        }
    }

    // Fake presenter to capture results
    private static class FakePresenter implements RunBacktestOutputBoundary {

        boolean successCalled = false;
        boolean failureCalled = false;

        RunBacktestOutputData data;
        String errorMessage;

        @Override
        public void present(RunBacktestOutputData outputData) {
            successCalled = true;
            data = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCalled = true;
            this.errorMessage = errorMessage;
        }
    }

    @Test
    void testSuccessfulBacktest() {
        FakeDAO dao = new FakeDAO();
        FakePresenter presenter = new FakePresenter();

        RunBacktestInteractor interactor =
                new RunBacktestInteractor(dao, presenter);

        RunBacktestInputData input = new RunBacktestInputData("test-project");
        interactor.execute(input);

        Assertions.assertTrue(presenter.successCalled);
        Assertions.assertFalse(presenter.failureCalled);

        Assertions.assertEquals("test-project", presenter.data.getProjectId());
        Assertions.assertTrue(presenter.data.getFinalValue() > 10000);
        Assertions.assertEquals(3, presenter.data.getEquityCurve().size());
    }

}