
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
    private final Map<Factor, Double> linearWeights;


    /**
     * Creates a new BacktestConfig.
     *
     * @param id                  A unique identifier for the backtest configuration.
     * @param configName          A user-friendly name for the configuration.
     * @param selectedFactors     A list of factors chosen for the backtest.
     * @param preprocessingMethod The preprocessing method to apply to factor scores.
     * @param linearWeights       A map of factors to their corresponding linear weights.
     * @throws IllegalArgumentException if id, configName, selectedFactors, or linearWeights are null or empty.
     */
    public BacktestConfig(String id, String configName, List<Factor> selectedFactors,
                          PreprocessingMethod preprocessingMethod, Map<Factor, Double> linearWeights) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        if (configName == null || configName.trim().isEmpty()) {
            throw new IllegalArgumentException("Config name cannot be null or empty.");
        }
        if (selectedFactors == null || selectedFactors.isEmpty()) {
            throw new IllegalArgumentException("Selected factors cannot be null or empty.");
        }
        if (linearWeights == null || linearWeights.isEmpty()) {
            throw new IllegalArgumentException("Linear weights cannot be null or empty.");
        }

        this.id = id;
        this.configName = configName;
        this.selectedFactors = selectedFactors;
        this.preprocessingMethod = preprocessingMethod;
        this.linearWeights = linearWeights;
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

    public Map<Factor, Double> getLinearWeights() {
        return linearWeights;
    }

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