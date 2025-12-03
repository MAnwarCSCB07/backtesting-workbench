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
    private final Map<String, Double> reversal = new HashMap<>();
    private final Map<String, Double> size = new HashMap<>();
    private final Map<String, Double> value = new HashMap<>();

    public InMemoryFactorDataGateway withMomentum(String symbol, double value) {
        momentum.put(symbol, value);
        return this;
    }

    public InMemoryFactorDataGateway withVolatility(String symbol, double value) {
        volatility.put(symbol, value);
        return this;
    }

    public InMemoryFactorDataGateway withReversal(String symbol, double value) {
        reversal.put(symbol, value);
        return this;
    }

    public InMemoryFactorDataGateway withSize(String symbol, double marketCap) {
        size.put(symbol, marketCap);
        return this;
    }

    public InMemoryFactorDataGateway withValueProxy(String symbol, double v) {
        value.put(symbol, v);
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

    @Override
    public double reversal(String symbol) {
        return reversal.getOrDefault(symbol, 0.0);
    }

    @Override
    public double size(String symbol) {
        return size.getOrDefault(symbol, 0.0);
    }

    @Override
    public double valueProxy(String symbol) {
        return value.getOrDefault(symbol, 0.0);
    }

}
