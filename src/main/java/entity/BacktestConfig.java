package entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for a backtest, including rebalancing frequency, transaction costs,
 * position limits, and factor weights.
 */
public class BacktestConfig {
    private final String rebalanceFreq;
    private final double transactionCost;
    private final double positionCap;
    private final Map<String, Double> factorWeights;

    /**
     * Creates a new BacktestConfig.
     * @param rebalanceFreq the rebalancing frequency (e.g., "monthly", "quarterly")
     * @param transactionCost the transaction cost in basis points
     * @param positionCap the maximum position size (as a fraction, e.g., 0.1 for 10%)
     * @param factorWeights a map of factor names to their weights
     */
    public BacktestConfig(String rebalanceFreq, double transactionCost, double positionCap,
                          Map<String, Double> factorWeights) {
        if (rebalanceFreq == null || rebalanceFreq.isEmpty()) {
            throw new IllegalArgumentException("Rebalance frequency cannot be null or empty");
        }
        if (transactionCost < 0) {
            throw new IllegalArgumentException("Transaction cost cannot be negative");
        }
        if (positionCap <= 0 || positionCap > 1) {
            throw new IllegalArgumentException("Position cap must be between 0 and 1");
        }
        if (factorWeights == null) {
            throw new IllegalArgumentException("Factor weights cannot be null");
        }
        this.rebalanceFreq = rebalanceFreq;
        this.transactionCost = transactionCost;
        this.positionCap = positionCap;
        this.factorWeights = new HashMap<>(factorWeights); // Defensive copy
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

    /**
     * Returns a copy of the factor weights map.
     * @return a map of factor names to weights
     */
    public Map<String, Double> getFactorWeights() {
        return new HashMap<>(factorWeights);
    }

    /**
     * Gets the weight for a specific factor.
     * @param factorName the name of the factor
     * @return the weight, or null if the factor doesn't exist
     */
    public Double getFactorWeight(String factorName) {
        return factorWeights.get(factorName);
    }
}

