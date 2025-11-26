
package entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents the configuration for a backtest, including selected factors,
 * preprocessing steps, and linear weights.
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
    private final Map<String, Double> factorWeights;


    /**
     * Creates a new BacktestConfig.
     *
     * @param id                  A unique identifier for the backtest configuration.
     * @param configName          A user-friendly name for the configuration.
     * @param selectedFactors     A list of factors chosen for the backtest.
     * @param preprocessingMethod The preprocessing method to apply to factor scores.
     * @param rebalanceFreq       The rebalancing frequency (e.g., "monthly", "weekly").
     * @param transactionCost     The transaction cost per trade (as a decimal, e.g., 0.001 for 10 bps).
     * @param positionCap         The maximum position size per asset (as a decimal between 0 and 1).
     * @param factorWeights       A map of factor names to their corresponding weights.
     * @throws IllegalArgumentException if required fields are null/empty or values out of range.
     */
    public BacktestConfig(String id, String configName, List<Factor> selectedFactors,
                          PreprocessingMethod preprocessingMethod,
                          String rebalanceFreq,
                          double transactionCost,
                          double positionCap,
                          Map<String, Double> factorWeights) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        if (configName == null || configName.trim().isEmpty()) {
            throw new IllegalArgumentException("Config name cannot be null or empty.");
        }
        if (selectedFactors == null || selectedFactors.isEmpty()) {
            throw new IllegalArgumentException("Selected factors cannot be null or empty.");
        }
        if (rebalanceFreq == null || rebalanceFreq.trim().isEmpty()) {
            throw new IllegalArgumentException("Rebalance frequency cannot be null or empty.");
        }
        if (transactionCost < 0) {
            throw new IllegalArgumentException("Transaction cost cannot be negative.");
        }
        if (positionCap <= 0 || positionCap > 1) {
            throw new IllegalArgumentException("Position cap must be in the range (0, 1].");
        }
        if (factorWeights == null || factorWeights.isEmpty()) {
            throw new IllegalArgumentException("Factor weights cannot be null or empty.");
        }

        this.id = id;
        this.configName = configName;
        this.selectedFactors = selectedFactors;
        this.preprocessingMethod = preprocessingMethod;
        this.rebalanceFreq = rebalanceFreq;
        this.transactionCost = transactionCost;
        this.positionCap = positionCap;
        this.factorWeights = factorWeights;
    }

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

    public String getRebalanceFreq() { return rebalanceFreq; }

    public double getTransactionCost() { return transactionCost; }

    public double getPositionCap() { return positionCap; }

    public Map<String, Double> getFactorWeights() { return factorWeights; }

    /**
     * Enum for available factors for backtesting.
     */
    public enum Factor implements Serializable {
        MOMENTUM_12_1,
        REVERSAL_1_1,
        SIZE,
        VALUE_PROXY,
        LOW_VOL
    }

    /**
     * Enum for available preprocessing methods.
     */
    public enum PreprocessingMethod {
        WINSORIZE,
        Z_SCORE,
        NONE
    }
}