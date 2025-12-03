package entity.factors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Momentum 12-1 raw score calculator.
 */
public class MomentumFactor implements FactorCalculator {
    public static final String KEY = "MOMENTUM_12_1";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Map<String, Double> compute(List<String> symbols, FactorDataGateway gateway) {
        Map<String, Double> out = new HashMap<>();
        for (String sym : symbols) {
            out.put(sym, gateway.momentum12m1(sym));
        }
        return out;
    }
}
