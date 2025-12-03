package entity.factors;

/**
 * Constants for factor data keys used with FactorDataGateway.
 * This allows for type-safe access to factor data while maintaining flexibility.
 */
public final class FactorDataKeys {

    /**
     * 12-month momentum skipping the most recent month (12-1) — higher is better.
     */
    public static final String MOMENTUM_12M1 = "momentum12m1";
    /**
     * Realized volatility proxy (e.g., annualized). Lower is better; calculators may invert if needed.
     */
    public static final String VOLATILITY = "volatility";
    /**
     * Short-term reversal factor.
     */
    public static final String REVERSAL = "reversal";
    /**
     * Company size proxy, typically market capitalization in absolute units.
     * Implementations should return a positive value when available; 0.0 if unknown.
     */
    public static final String SIZE = "size";
    /**
     * Value proxy where higher values indicate a cheaper valuation.
     * For example: book-to-market (1/PB) or earnings yield (1/PE).
     */
    public static final String VALUE_PROXY = "valueProxy";

    private FactorDataKeys() {
        // Prevent instantiation
    }
}

