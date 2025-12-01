package entity;

import java.util.Map;

/**
 * Entity representing the configuration for a backtest.
 * Contains rebalancing frequency, transaction costs, position caps, and factor weights.
 */
public class BacktestConfig {
    private final String rebalanceFreq; // e.g., "monthly", "quarterly", "weekly"
    private final double transactionCost; // in basis points (bps)
    private final double positionCap; // maximum position size
    private final Map<String, Double> factorWeights; // Map of factor name -> weight

    /**
     * Creates a BacktestConfig with the given parameters.
     * @param rebalanceFreq rebalancing frequency
     * @param transactionCost transaction cost in basis points
     * @param positionCap maximum position size
     * @param factorWeights map of factor names to their weights
     * @throws IllegalArgumentException if rebalanceFreq is empty or transactionCost/positionCap are negative
     */
    public BacktestConfig(String rebalanceFreq, double transactionCost, double positionCap, 
                          Map<String, Double> factorWeights) {
        if (rebalanceFreq == null || "".equals(rebalanceFreq.trim())) {
            throw new IllegalArgumentException("Rebalance frequency cannot be empty");
        }
        if (transactionCost < 0) {
            throw new IllegalArgumentException("Transaction cost cannot be negative");
        }
        if (positionCap < 0) {
            throw new IllegalArgumentException("Position cap cannot be negative");
        }
        this.rebalanceFreq = rebalanceFreq;
        this.transactionCost = transactionCost;
        this.positionCap = positionCap;
        this.factorWeights = factorWeights;
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

    public Map<String, Double> getFactorWeights() {
        return factorWeights;
    }
}

