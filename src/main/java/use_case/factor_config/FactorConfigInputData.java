package use_case.factor_config;

import entity.BacktestConfig.Factor;
import entity.BacktestConfig.PreprocessingMethod;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FactorConfigInputData {
    private final List<String> symbols;
    private final List<Factor> selectedFactors;
    private final Map<Factor, Double> weights;
    private final PreprocessingMethod preprocessing;

    public FactorConfigInputData(List<String> symbols,
                                 List<Factor> selectedFactors,
                                 Map<Factor, Double> weights,
                                 PreprocessingMethod preprocessing) {
        this.symbols = List.copyOf(symbols);
        this.selectedFactors = List.copyOf(selectedFactors);
        this.weights = Collections.unmodifiableMap(new EnumMap<>(weights));
        this.preprocessing = preprocessing;
    }

    public List<String> getSymbols() { return symbols; }
    public List<Factor> getSelectedFactors() { return selectedFactors; }
    public Map<Factor, Double> getWeights() { return weights; }
    public PreprocessingMethod getPreprocessing() { return preprocessing; }
}
