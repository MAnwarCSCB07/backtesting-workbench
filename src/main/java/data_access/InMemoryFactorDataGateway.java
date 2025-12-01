package data_access;

import entity.factors.FactorDataGateway;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory implementation of FactorDataGateway for testing/demo.
 */
public class InMemoryFactorDataGateway implements FactorDataGateway {

    private final Map<String, Double> momentum = new HashMap<>();
    private final Map<String, Double> volatility = new HashMap<>();

    public InMemoryFactorDataGateway withMomentum(String symbol, double value) {
        momentum.put(symbol, value);
        return this;
    }

    public InMemoryFactorDataGateway withVolatility(String symbol, double value) {
        volatility.put(symbol, value);
        return this;
    }

    @Override
    public double momentum12m1(String symbol) {
        return momentum.getOrDefault(symbol, 0.0);
    }

    @Override
    public double volatility(String symbol) {
        return volatility.getOrDefault(symbol, 0.0);
    }
}
