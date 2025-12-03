package entity;

import java.util.List;

public class BacktestResult {

    private final String projectId;
    private final double finalValue;
    private final double maxDrawdown;
    private final List<Double> equityCurve;

    public BacktestResult(String projectId,
                          double finalValue,
                          double maxDrawdown,
                          List<Double> equityCurve) {
        this.projectId = projectId;
        this.finalValue = finalValue;
        this.maxDrawdown = maxDrawdown;
        this.equityCurve = equityCurve;
    }

    public String getProjectId() {
        return projectId;
    }

    public double getFinalValue() {
        return finalValue;
    }

    public double getMaxDrawdown() {
        return maxDrawdown;
    }

    public List<Double> getEquityCurve() {
        return equityCurve;
    }
}
