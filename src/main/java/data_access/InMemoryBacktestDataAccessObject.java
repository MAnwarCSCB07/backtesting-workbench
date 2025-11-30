package data_access;

import entity.BacktestConfig;
import entity.Universe;
import entity.PriceBar;
import entity.BacktestResult;

import java.time.LocalDate;
import java.util.*;

public class InMemoryBacktestDataAccessObject implements BacktestDataAccessInterface {

    private final Map<String, BacktestConfig> configs = new HashMap<>();
    private final Map<String, Universe> universes = new HashMap<>();
    private final Map<String, List<PriceBar>> priceData = new HashMap<>();
    private final Map<String, BacktestResult> results = new HashMap<>();

    public InMemoryBacktestDataAccessObject() {
        // Temporary hardcoded data so your Interactor can run tests later.

        // Example project "demo-project"
        configs.put("demo-project", new BacktestConfig(
                "demo-project",
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 10),
                10000.0,
                "buy_and_hold"
        ));

        universes.put("demo-project", new Universe(
                List.of("AAPL")
        ));

        // Fake price series for AAPL from Jan 1 to Jan 10
        List<PriceBar> bars = new ArrayList<>();
        LocalDate d = LocalDate.of(2020, 1, 1);

        for (int i = 0; i < 10; i++) {
            bars.add(new PriceBar(
                    "AAPL",
                    d.plusDays(i),
                    100 + i,
                    101 + i,
                    99 + i,
                    100 + i,
                    1_000_000L
            ));
        }

        priceData.put("AAPL", bars);
    }

    @Override
    public BacktestConfig getConfig(String projectId) {
        return configs.get(projectId);
    }

    @Override
    public Universe getUniverse(String projectId) {
        return universes.get(projectId);
    }

    @Override
    public List<PriceBar> getPriceSeries(String ticker, LocalDate start, LocalDate end) {
        return priceData.getOrDefault(ticker, new ArrayList<>());
    }

    @Override
    public void saveResult(BacktestResult result) {
        results.put(result.getProjectId(), result);
    }
}