package use_case.factor_config;

import java.util.List;
import java.util.Map;

/**
 * Output DTO carrying ranked results. The scores are standardized and combined.
 */
public class FactorConfigOutputData {
    public static class Row {
        public final String symbol;
        public final double composite;
        public final Map<String, Double> zScoresByFactor; // factor key -> z-scored value

        public Row(String symbol, double composite, Map<String, Double> zScoresByFactor) {
            this.symbol = symbol;
            this.composite = composite;
            this.zScoresByFactor = zScoresByFactor;
        }
    }

    private final List<Row> ranked;

    public FactorConfigOutputData(List<Row> ranked) {
        this.ranked = ranked;
    }

    public List<Row> getRanked() {
        return ranked;
    }
}
