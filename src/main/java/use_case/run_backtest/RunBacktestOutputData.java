package use_case.run_backtest;

import java.util.List;

/**
 * Output data for the Run Backtest use case.
 * Contains summary performance metrics and the equity curve.
 */
public class RunBacktestOutputData {

    private final double finalValue;
    private final double maxDrawdown;
    private final double totalReturn;
    private final double annualizedReturn;
    private final double volatilityAnnualized;
    private final double sharpeRatio;
    private final double worstDailyLossPercent;
    private final List<Double> equityCurve;

    public RunBacktestOutputData(double finalValue,
                                 double maxDrawdown,
                                 double totalReturn,
                                 double annualizedReturn,
                                 double volatilityAnnualized,
                                 double sharpeRatio,
                                 double worstDailyLossPercent,
                                 List<Double> equityCurve) {
        this.finalValue = finalValue;
        this.maxDrawdown = maxDrawdown;
        this.totalReturn = totalReturn;
        this.annualizedReturn = annualizedReturn;
        this.volatilityAnnualized = volatilityAnnualized;
        this.sharpeRatio = sharpeRatio;
        this.worstDailyLossPercent = worstDailyLossPercent;
        this.equityCurve = equityCurve;
    }
    

    public double getFinalValue() {
        return finalValue;
    }

    public double getMaxDrawdown() {
        return maxDrawdown;
    }

    public double getTotalReturn() {
        return totalReturn;
    }

    public double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public double getVolatilityAnnualized() {
        return volatilityAnnualized;
    }

    public double getSharpeRatio() {
        return sharpeRatio;
    }

    public double getWorstDailyLossPercent() {
        return worstDailyLossPercent;
    }

    public List<Double> getEquityCurve() {
        return equityCurve;
    }
}