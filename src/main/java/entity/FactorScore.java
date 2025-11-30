package entity;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import entity.BacktestConfig.Factor; // Import the Factor enum

/**
 * Represents the "Report Card" for a specific stock.
 * Contains the raw input data (scores) and the final calculated grade (composite).
 */
public class FactorScore {

    private final String symbol;
    // Changed key from String to Factor for safety
    private final Map<Factor, Double> scores;
    private final double composite;

    /**
     * @param symbol    The ticker (e.g., "TSLA")
     * @param scores    The raw factor values (e.g., Momentum = 1.5)
     * @param composite The weighted sum calculated by the BacktestConfig
     */
    public FactorScore(String symbol, Map<Factor, Double> scores, double composite) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty.");
        }
        if (scores == null) {
            throw new IllegalArgumentException("Scores map cannot be null.");
        }

        this.symbol = symbol;
        // Defensive copy using EnumMap
        this.scores = Collections.unmodifiableMap(new EnumMap<>(scores));
        this.composite = composite;
    }

    public String getSymbol() {
        return symbol;
    }

    public Map<Factor, Double> getScores() {
        return scores;
    }

    public double getComposite() {
        return composite;
    }

    @Override
    public String toString() {
        return String.format("Symbol: %s | Composite: %.4f | Details: %s",
                symbol, composite, scores.toString());
    }
}