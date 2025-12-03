package entity.factors;

/**
 * Data access abstraction for factor calculators.
 * Implementations can load from APIs, files, or in-memory test data.
 * <p>
 * This interface follows the Open-Closed Principle by using a generic
 * data retrieval method rather than specific methods for each factor.
 * New factors can be added without modifying this interface.
 */
public interface FactorDataGateway {

    /**
     * Retrieve a specific data point for a given symbol and data key.
     *
     * @param symbol  the stock symbol (e.g., "AAPL")
     * @param dataKey the type of data to retrieve (e.g., "momentum12m1", "volatility", "size")
     * @return the value for the requested data point, or 0.0 if not available
     */
    double getData(String symbol, String dataKey);

    /**
     * Check if the gateway supports a particular data key.
     *
     * @param dataKey the data key to check
     * @return true if this gateway can provide data for the given key
     */
    boolean supportsDataKey(String dataKey);
}
