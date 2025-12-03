package data_access;

import entity.factors.FactorDataGateway;
import entity.factors.FactorDataKeys;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory implementation of FactorDataGateway for testing/demo.
 * This implementation uses a map-of-maps structure to allow flexible data storage
 * without requiring modification when new factors are added.
 */
public class InMemoryFactorDataGateway implements FactorDataGateway {

    // Maps data key -> (symbol -> value)
    private final Map<String, Map<String, Double>> dataStore = new HashMap<>();

    public InMemoryFactorDataGateway() {
        // Initialize with known data keys
        dataStore.put(FactorDataKeys.MOMENTUM_12M1, new HashMap<>());
        dataStore.put(FactorDataKeys.VOLATILITY, new HashMap<>());
        dataStore.put(FactorDataKeys.REVERSAL, new HashMap<>());
        dataStore.put(FactorDataKeys.SIZE, new HashMap<>());
        dataStore.put(FactorDataKeys.VALUE_PROXY, new HashMap<>());
    }

    // Builder-style methods for setting up test data
    public InMemoryFactorDataGateway withMomentum(String symbol, double value) {
        dataStore.get(FactorDataKeys.MOMENTUM_12M1).put(symbol, value);
        return this;
    }

    public InMemoryFactorDataGateway withVolatility(String symbol, double value) {
        dataStore.get(FactorDataKeys.VOLATILITY).put(symbol, value);
        return this;
    }

    public InMemoryFactorDataGateway withReversal(String symbol, double value) {
        dataStore.get(FactorDataKeys.REVERSAL).put(symbol, value);
        return this;
    }

    public InMemoryFactorDataGateway withSize(String symbol, double marketCap) {
        dataStore.get(FactorDataKeys.SIZE).put(symbol, marketCap);
        return this;
    }

    public InMemoryFactorDataGateway withValueProxy(String symbol, double v) {
        dataStore.get(FactorDataKeys.VALUE_PROXY).put(symbol, v);
        return this;
    }

    /**
     * Generic data setter for flexibility in tests.
     *
     * @param dataKey the type of data
     * @param symbol  the stock symbol
     * @param value   the data value
     * @return this instance for method chaining
     */
    public InMemoryFactorDataGateway withData(String dataKey, String symbol, double value) {
        dataStore.computeIfAbsent(dataKey, k -> new HashMap<>()).put(symbol, value);
        return this;
    }

    @Override
    public double getData(String symbol, String dataKey) {
        Map<String, Double> symbolMap = dataStore.get(dataKey);
        if (symbolMap == null) {
            return 0.0;
        }
        return symbolMap.getOrDefault(symbol, 0.0);
    }

    @Override
    public boolean supportsDataKey(String dataKey) {
        return dataStore.containsKey(dataKey);
    }

}
