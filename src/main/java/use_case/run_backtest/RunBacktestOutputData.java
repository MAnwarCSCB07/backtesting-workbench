package use_case.run_backtest;

public class RunBacktestOutputData {

    private final double finalValue;
    private final double maxDrawdown;
    private final double totalReturn; // NEW FIELD

    public RunBacktestOutputData(double finalValue, double maxDrawdown, double totalReturn) {
        this.finalValue = finalValue;
        this.maxDrawdown = maxDrawdown;
        this.totalReturn = totalReturn;
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
}