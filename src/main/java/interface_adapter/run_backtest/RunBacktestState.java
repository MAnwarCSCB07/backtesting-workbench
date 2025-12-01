package interface_adapter.run_backtest;

import java.util.ArrayList;
import java.util.List;

/**
 * View-model state for the Run Backtest screen.
 */
public class RunBacktestState {

    private String statusMessage;

    private Double finalValue;
    private Double maxDrawdown;
    private Double totalReturn;
    private Double annualizedReturn;
    private Double volatility;
    private Double sharpeRatio;
    private Double worstDailyLoss;

    // NEW: equity curve
    private List<Double> equityCurve = new ArrayList<>();

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Double getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(Double finalValue) {
        this.finalValue = finalValue;
    }

    public Double getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(Double maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public Double getTotalReturn() {
        return totalReturn;
    }

    public void setTotalReturn(Double totalReturn) {
        this.totalReturn = totalReturn;
    }

    public Double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(Double annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    public Double getVolatility() {
        return volatility;
    }

    public void setVolatility(Double volatility) {
        this.volatility = volatility;
    }

    public Double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Double getWorstDailyLoss() {
        return worstDailyLoss;
    }

    public void setWorstDailyLoss(Double worstDailyLoss) {
        this.worstDailyLoss = worstDailyLoss;
    }

    public List<Double> getEquityCurve() {
        return equityCurve;
    }

    public void setEquityCurve(List<Double> equityCurve) {
        this.equityCurve = equityCurve;
    }
}