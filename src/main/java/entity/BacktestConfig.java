package entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the configuration for a backtest.
 * Defines which factors define the strategy and how important each factor is (weights).
 */
public class BacktestConfig {

    private final String id;
    private final String configName;
    private final List<Factor> selectedFactors;
    private final PreprocessingMethod preprocessingMethod;

    // Backtest parameters
    private final String rebalanceFreq;
    private final double transactionCost;
    private final double positionCap;

    // How much weight to give each factor across the board
    private final Map<Factor, Double> factorWeights;

    /**
     * Creates a new BacktestConfig.
     */
    public BacktestConfig(String id,
                          String configName,
                          List<Factor> selectedFactors,
                          PreprocessingMethod preprocessingMethod,
                          String rebalanceFreq,
                          double transactionCost,
                          double positionCap,
                          Map<Factor, Double> factorWeights) {

        validateInputs(id, configName, selectedFactors, rebalanceFreq, transactionCost, positionCap, factorWeights);

        this.id = id;
        this.configName = configName;
        // Create an unmodifiable copy to prevent external mutation
        this.selectedFactors = List.copyOf(selectedFactors);
        this.preprocessingMethod = preprocessingMethod;
        this.rebalanceFreq = rebalanceFreq;
        this.transactionCost = transactionCost;
        this.positionCap = positionCap;

        // Defensive copy into an EnumMap for performance and type safety
        this.factorWeights = Collections.unmodifiableMap(new EnumMap<>(factorWeights));
    }

    private void validateInputs(String id, String configName, List<Factor> selectedFactors,
                                String rebalanceFreq, double transactionCost, double positionCap,
                                Map<Factor, Double> factorWeights) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID cannot be empty.");
        if (configName == null || configName.trim().isEmpty())
            throw new IllegalArgumentException("Config name cannot be empty.");
        if (selectedFactors == null || selectedFactors.isEmpty())
            throw new IllegalArgumentException("Must select at least one factor.");
        if (factorWeights == null || factorWeights.isEmpty())
            throw new IllegalArgumentException("Factor weights cannot be empty.");
        if (transactionCost < 0) throw new IllegalArgumentException("Transaction cost cannot be negative.");
        if (positionCap <= 0 || positionCap > 1)
            throw new IllegalArgumentException("Position cap must be between 0 and 1.");

        // Logical check: Ensure every selected factor has a weight
        for (Factor f : selectedFactors) {
            if (!factorWeights.containsKey(f)) {
                throw new IllegalArgumentException("Missing weight for selected factor: " + f);
            }
        }
    }

    /**
     * Calculates the composite score for a specific symbol based on this Config's weights.
     * * Formula: Sum(Weight * Score)
     *
     * @param symbol    The ticker symbol (e.g., "AAPL")
     * @param rawScores The raw values for this specific stock (e.g., Momentum=1.2)
     * @return A specific FactorScore object containing the result.
     */
    public FactorScore calculateCompositeScore(String symbol, Map<Factor, Double> rawScores) {
        if (rawScores == null || rawScores.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate composite: raw scores are empty.");
        }

        double composite = 0.0;

        // Iterate only through the factors defined in THIS configuration (The Weights)
        // If the stock has extra data we don't care about, ignore it.
        // If the stock is missing data we need, treat it as 0.0 (or handle as an error).
        for (Map.Entry<Factor, Double> entry : factorWeights.entrySet()) {
            Factor factor = entry.getKey();
            double weight = entry.getValue();

            // Get the score for this factor, default to 0.0 if data is missing for this stock
            double score = rawScores.getOrDefault(factor, 0.0);

            composite += (weight * score);
        }

        return new FactorScore(symbol, rawScores, composite);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getConfigName() {
        return configName;
    }

    public List<Factor> getSelectedFactors() {
        return selectedFactors;
    }

    public PreprocessingMethod getPreprocessingMethod() {
        return preprocessingMethod;
    }

    public String getRebalanceFreq() {
        return rebalanceFreq;
    }

    public double getTransactionCost() {
        return transactionCost;
    }

    public double getPositionCap() {
        return positionCap;
    }

    public Map<Factor, Double> getFactorWeights() {
        return factorWeights;
    }

    // Enums

    public enum Factor implements Serializable {
        MOMENTUM_12_1,
        REVERSAL_1_1,
        SIZE,
        VALUE_PROXY,
        LOW_VOL
    }

    public enum PreprocessingMethod {
        WINSORIZE,
        Z_SCORE,
        NONE
    }
}