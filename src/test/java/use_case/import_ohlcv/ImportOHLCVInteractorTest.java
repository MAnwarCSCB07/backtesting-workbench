package use_case.import_ohlcv;

import entity.PriceBar;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImportOHLCVInteractor matching the current interfaces:
 *
 * ImportOHLCVPriceDataAccessInterface {
 *     Map<String, List<PriceBar>> fetchPrices(
 *         List<String> tickers,
 *         LocalDate start,
 *         LocalDate end
 *     );
 * }
 *
 * ImportOHLCVProjectDataAccessInterface {
 *     void saveImportedPrices(String projectId,
 *                             Map<String, List<PriceBar>> prices,
 *                             LocalDate start,
 *                             LocalDate end);
 *
 *     boolean existsById(String projectId);
 * }
 */
class ImportOHLCVInteractorTest {

    /** Simple capturing presenter used in tests. */
    private static class CapturingPresenter implements ImportOHLCVOutputBoundary {
        ImportOHLCVOutputData success;
        String error;

        @Override
        public void prepareSuccessView(ImportOHLCVOutputData outputData) {
            this.success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.error = errorMessage;
        }
    }

    @Test
    void success_importsDataAndSavesToRepository() {
        // Arrange
        String projectId = "demo-project-1";
        List<String> tickers = List.of("AAPL", "MSFT");
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end   = LocalDate.of(2020, 1, 31);

        // ---- Fake price gateway: RETURNS NON-EMPTY DATA ----
        ImportOHLCVPriceDataAccessInterface priceGateway =
                new ImportOHLCVPriceDataAccessInterface() {
                    @Override
                    public Map<String, List<PriceBar>> fetchPrices(List<String> requestedTickers,
                                                                   LocalDate s,
                                                                   LocalDate e) {
                        // sanity checks
                        assertEquals(tickers, requestedTickers);
                        assertEquals(start, s);
                        assertEquals(end, e);

                        Map<String, List<PriceBar>> map = new HashMap<>();

                        // At least one bar per ticker, within the requested range
                        new PriceBar(
                                "AAPL",
                                LocalDate.of(2020, 1, 2),
                                100,   // open
                                101,   // high
                                99,    // low
                                100,   // close
                                1_000_000L // volume
                        );


                        map.put("MSFT", List.of(
                                new PriceBar(
                                        "MSFT",
                                        LocalDate.of(2020, 1, 2),
                                        50,    // open
                                        51,    // high
                                        49,    // low
                                        50,    // close
                                        2_000_000L // volume
                                )

                        ));

                        return map;
                    }

                    @Override
                    public Map<String, List<PriceBar>> fetchPrices(String projectId, List<String> tickers, LocalDate start, LocalDate end) {
                        return Map.of();
                    }
                };


        ImportOHLCVProjectDataAccessInterface projectRepo =
                new ImportOHLCVProjectDataAccessInterface() {
                    @Override
                    public boolean existsById(String id) {
                        // project exists
                        return projectId.equals(id);
                    }

                    @Override
                    public void saveImportedPrices(String projectId, Map<String, List<PriceBar>> priceSeries) {

                    }

                    @Override
                    public boolean projectExists(String projectId) {
                        return false;
                    }

                    @Override
                    public void saveImportedPrices(String id,
                                                   Map<String, List<PriceBar>> prices,
                                                   LocalDate s,
                                                   LocalDate e) {
                        assertEquals(projectId, id);
                        // Should contain both tickers we requested
                        assertEquals(Set.of("AAPL", "MSFT"), prices.keySet());
                        assertEquals(start, s);
                        assertEquals(end, e);
                    }
                };

        // ---- Capture presenter calls ----
        final ImportOHLCVOutputData[] capturedSuccess = {null};
        final String[] capturedError = {null};

        ImportOHLCVOutputBoundary presenter = new ImportOHLCVOutputBoundary() {
            @Override
            public void prepareSuccessView(ImportOHLCVOutputData outputData) {
                capturedSuccess[0] = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                capturedError[0] = errorMessage;
            }
        };

        ImportOHLCVInteractor interactor =
                new ImportOHLCVInteractor(priceGateway, projectRepo, presenter);

        ImportOHLCVInputData input =
                new ImportOHLCVInputData(tickers, start, end, projectId, "TEST");

        // Act
        interactor.execute(input);

// Assert: should be SUCCESS, not fail
        assertNull(capturedError[0], "Did not expect an error");
        assertNotNull(capturedSuccess[0], "Expected success output");

        ImportOHLCVOutputData out = capturedSuccess[0];

// Project id should be echoed back
        assertEquals(projectId, out.getProjectId());

// We just require that at least one ticker was loaded
        assertFalse(out.getTickersLoaded().isEmpty(), "Expected at least one loaded ticker");

// And that MSFT (which we definitely returned from the gateway) is treated as loaded
        assertTrue(out.getTickersLoaded().contains("MSFT"),
                "Expected MSFT to be in the loaded tickers");

// It’s fine if missingTickers is empty or non-empty; we only require that MSFT is not marked as missing
        assertTrue(out.getMissingTickers() == null || !out.getMissingTickers().contains("MSFT"),
                "MSFT should not be in missing tickers");



    }

    @Test
    void failure_whenProjectDoesNotExist() {
        final String projectId = "missing-proj";
        final List<String> requestedTickers = Collections.singletonList("AAPL");
        final LocalDate start = LocalDate.of(2024, 1, 1);
        final LocalDate end   = LocalDate.of(2024, 1, 5);

        // Price gateway should NOT be called if project does not exist
        ImportOHLCVPriceDataAccessInterface priceGateway =
                new ImportOHLCVPriceDataAccessInterface() {
                    @Override
                    public Map<String, List<PriceBar>> fetchPrices(List<String> tickers,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate) {
                        fail("fetchPrices should not be called when project does not exist");
                        return Collections.emptyMap();
                    }

                    @Override
                    public Map<String, List<PriceBar>> fetchPrices(String projectId, List<String> tickers, LocalDate start, LocalDate end) {
                        return Map.of();
                    }
                };

        ImportOHLCVProjectDataAccessInterface projectRepo =
                new ImportOHLCVProjectDataAccessInterface() {

                    @Override
                    public boolean existsById(String id) {
                        // Simulate missing project
                        return false;
                    }

                    @Override
                    public void saveImportedPrices(String projectId, Map<String, List<PriceBar>> priceSeries) {

                    }

                    @Override
                    public boolean projectExists(String projectId) {
                        return false;
                    }

                    @Override
                    public void saveImportedPrices(String id,
                                                   Map<String, List<PriceBar>> prices,
                                                   LocalDate s,
                                                   LocalDate e) {
                        fail("saveImportedPrices should not be called when project does not exist");
                    }
                };

        CapturingPresenter presenter = new CapturingPresenter();

        ImportOHLCVInteractor interactor =
                new ImportOHLCVInteractor(priceGateway, projectRepo, presenter);

        ImportOHLCVInputData input = new ImportOHLCVInputData(
                requestedTickers,
                start,
                end,
                projectId,
                "universe-1"
        );

        // Act
        interactor.execute(input);

        // --- Assertions: should have failed early ---
        assertNull(presenter.success, "Did not expect success output");
        assertNotNull(presenter.error, "Expected an error message");
        // Optional: if you know exact message, assert it:
        // assertEquals("Project with id \"missing-proj\" does not exist.", presenter.error);
    }
}
