package entity.factors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Value proxy factor. Higher returned value means cheaper valuation (better).
 */
public class ValueProxyFactor implements FactorCalculator {
    public static final String KEY = "VALUE_PROXY";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Map<String, Double> compute(List<String> symbols, FactorDataGateway gateway) {
        Map<String, Double> out = new HashMap<>();
        for (String sym : symbols) {
            out.put(sym, gateway.getData(sym, FactorDataKeys.VALUE_PROXY));
        }
        return out;
    }
}
