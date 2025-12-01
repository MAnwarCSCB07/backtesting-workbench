package entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents factor scores for a single security, including individual factor scores
 * and a composite score.
 */
public class FactorScore {
    private final String symbol;
    private final Map<String, Double> scores;
    private final double composite;

    /**
     * Creates a new FactorScore.
     * @param symbol the stock symbol
     * @param scores a map of factor names to their scores
     * @param composite the composite (weighted) score
     */
    public FactorScore(String symbol, Map<String, Double> scores, double composite) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (scores == null) {
            throw new IllegalArgumentException("Scores map cannot be null");
        }
        this.symbol = symbol;
        this.scores = new HashMap<>(scores); // Defensive copy
        this.composite = composite;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns a copy of the scores map.
     * @return a map of factor names to scores
     */
    public Map<String, Double> getScores() {
        return new HashMap<>(scores);
    }

    public double getComposite() {
        return composite;
    }

    /**
     * Gets the score for a specific factor.
     * @param factorName the name of the factor
     * @return the score, or null if the factor doesn't exist
     */
    public Double getScore(String factorName) {
        return scores.get(factorName);
    }
}


