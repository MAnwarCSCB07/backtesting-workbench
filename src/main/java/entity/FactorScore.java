package entity;

import java.util.Map;

/**
 * Entity representing a factor score for a security.
 * Contains individual factor scores and a composite score.
 */
public class FactorScore {
    private final String symbol;
    private final Map<String, Double> scores; // Map of factor name -> score
    private final double composite; // Weighted composite score

    /**
     * Creates a FactorScore for a given symbol.
     * @param symbol the ticker symbol
     * @param scores map of factor names to their scores
     * @param composite the weighted composite score
     * @throws IllegalArgumentException if symbol is empty or null
     */
    public FactorScore(String symbol, Map<String, Double> scores, double composite) {
        if (symbol == null || "".equals(symbol.trim())) {
            throw new IllegalArgumentException("Symbol cannot be empty");
        }
        this.symbol = symbol;
        this.scores = scores;
        this.composite = composite;
    }

    public String getSymbol() {
        return symbol;
    }

    public Map<String, Double> getScores() {
        return scores;
    }

    public double getComposite() {
        return composite;
    }
}

