package interface_adapter.factor_config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FactorViewState {
    public static class RowVM {
        public final String symbol;
        public final double composite;
        public final Map<String, Double> zScores;

        public RowVM(String symbol, double composite, Map<String, Double> zScores) {
            this.symbol = symbol;
            this.composite = composite;
            this.zScores = zScores;
        }
    }

    private List<RowVM> ranked = new ArrayList<>();
    private String error;

    public List<RowVM> getRanked() { return ranked; }
    public void setRanked(List<RowVM> ranked) { this.ranked = ranked; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
