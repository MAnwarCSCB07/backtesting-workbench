package entity.factors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Short-term reversal 1-1 factor. Higher recent return => higher score.
 */
public class ReversalFactor implements FactorCalculator {
    public static final String KEY = "REVERSAL_1_1";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Map<String, Double> compute(List<String> symbols, FactorDataGateway gateway) {
        Map<String, Double> out = new HashMap<>();
        for (String sym : symbols) {
            out.put(sym, gateway.getData(sym, FactorDataKeys.REVERSAL));
        }
        return out;
    }
}
