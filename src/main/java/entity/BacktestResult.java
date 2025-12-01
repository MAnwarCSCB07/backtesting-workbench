package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Results from running a backtest, including equity curve, drawdown series,
 * and performance metrics.
 */
public class BacktestResult {
    private final List<Double> equityCurve;
    private final List<Double> drawdown;
    private final Map<String, Double> metrics;

    /**
     * Creates a new BacktestResult.
     * @param equityCurve the equity curve over time
     * @param drawdown the drawdown series over time
     * @param metrics a map of metric names to their values (e.g., "Sharpe Ratio", "CAGR", "Max Drawdown")
     */
    public BacktestResult(List<Double> equityCurve, List<Double> drawdown, Map<String, Double> metrics) {
        if (equityCurve == null) {
            throw new IllegalArgumentException("Equity curve cannot be null");
        }
        if (drawdown == null) {
            throw new IllegalArgumentException("Drawdown cannot be null");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("Metrics cannot be null");
        }
        this.equityCurve = new ArrayList<>(equityCurve); // Defensive copy
        this.drawdown = new ArrayList<>(drawdown); // Defensive copy
        this.metrics = new HashMap<>(metrics); // Defensive copy
    }

    /**
     * Returns a copy of the equity curve.
     * @return the equity curve as a list of values
     */
    public List<Double> getEquityCurve() {
        return new ArrayList<>(equityCurve);
    }

    /**
     * Returns a copy of the drawdown series.
     * @return the drawdown series as a list of values
     */
    public List<Double> getDrawdown() {
        return new ArrayList<>(drawdown);
    }

    /**
     * Returns a copy of the metrics map.
     * @return a map of metric names to values
     */
    public Map<String, Double> getMetrics() {
        return new HashMap<>(metrics);
    }

    /**
     * Gets a specific metric value.
     * @param metricName the name of the metric
     * @return the metric value, or null if the metric doesn't exist
     */
    public Double getMetric(String metricName) {
        return metrics.get(metricName);
    }
}

