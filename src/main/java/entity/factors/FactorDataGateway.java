package entity.factors;

/**
 * Data access abstraction for factor calculators.
 * Implementations can load from APIs, files, or in-memory test data.
 */
public interface FactorDataGateway {

    /**
     * 12-month momentum skipping the most recent month (12-1) — higher is better.
     */
    double momentum12m1(String symbol);

    /**
     * Realized volatility proxy (e.g., annualized). Lower is better; calculators may invert if needed.
     */
    double volatility(String symbol);
}
