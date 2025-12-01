package entity;

import java.util.List;
import java.util.Map;

/**
 * Entity representing the results of a backtest.
 * Contains equity curve, drawdown series, and performance metrics.
 */
public class BacktestResult {
    private final List<Double> equityCurve; // Portfolio value over time
    private final List<Double> drawdown; // Drawdown series
    private final Map<String, Double> metrics; // Performance metrics (Sharpe ratio, CAGR, etc.)

    /**
     * Creates a BacktestResult with the given data.
     * @param equityCurve list of portfolio values over time
     * @param drawdown list of drawdown values over time
     * @param metrics map of metric names to their values
     * @throws IllegalArgumentException if equityCurve or drawdown are null
     */
    public BacktestResult(List<Double> equityCurve, List<Double> drawdown, Map<String, Double> metrics) {
        if (equityCurve == null) {
            throw new IllegalArgumentException("Equity curve cannot be null");
        }
        if (drawdown == null) {
            throw new IllegalArgumentException("Drawdown cannot be null");
        }
        this.equityCurve = equityCurve;
        this.drawdown = drawdown;
        this.metrics = metrics;
    }

    public List<Double> getEquityCurve() {
        return equityCurve;
    }

    public List<Double> getDrawdown() {
        return drawdown;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }
}

