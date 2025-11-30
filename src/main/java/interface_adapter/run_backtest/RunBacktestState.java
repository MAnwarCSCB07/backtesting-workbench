package interface_adapter.run_backtest;

import java.util.List;

public class RunBacktestState {

    private String projectId = "";
    private String statusMessage = "";
    private List<Double> equityCurve;

    private Double finalValue;
    private Double maxDrawdown;

    public RunBacktestState() { }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public List<Double> getEquityCurve() { return equityCurve; }
    public void setEquityCurve(List<Double> equityCurve) { this.equityCurve = equityCurve; }

    public Double getFinalValue() { return finalValue; }
    public void setFinalValue(Double finalValue) { this.finalValue = finalValue; }

    public Double getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(Double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
}