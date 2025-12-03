package entity.factors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Low volatility factor. Returns negative volatility so that higher is better.
 */
public class LowVolFactor implements FactorCalculator {
    public static final String KEY = "LOW_VOL";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Map<String, Double> compute(List<String> symbols, FactorDataGateway gateway) {
        Map<String, Double> out = new HashMap<>();
        for (String sym : symbols) {
            out.put(sym, -gateway.getData(sym, FactorDataKeys.VOLATILITY)); // invert so higher is better
        }
        return out;
    }
}
