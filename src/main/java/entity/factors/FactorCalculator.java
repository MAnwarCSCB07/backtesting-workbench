package entity.factors;

import java.util.List;
import java.util.Map;

/**
 * Entity-level abstraction for computing a factor's raw scores for a set of symbols.
 */
public interface FactorCalculator {

    /**
     * @return the unique key identifying this factor (e.g., BACKTEST enum name)
     */
    String getKey();

    /**
     * Compute raw (unstandardized) scores for the provided symbols.
     * @param symbols symbols to compute factor for
     * @param gateway data gateway providing required raw data
     * @return map from symbol to raw score
     */
    Map<String, Double> compute(List<String> symbols, FactorDataGateway gateway);
}
