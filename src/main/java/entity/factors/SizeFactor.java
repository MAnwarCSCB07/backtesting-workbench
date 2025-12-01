package entity.factors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Size factor. Smaller companies should score higher.
 * We take negative log(size) so that higher is better (smaller cap -> larger score).
 */
public class SizeFactor implements FactorCalculator {
    public static final String KEY = "SIZE";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Map<String, Double> compute(List<String> symbols, FactorDataGateway gateway) {
        Map<String, Double> out = new HashMap<>();
        for (String sym : symbols) {
            double sz = gateway.size(sym);
            if (sz > 0) {
                out.put(sym, -Math.log(sz));
            } else {
                out.put(sym, 0.0);
            }
        }
        return out;
    }
}
