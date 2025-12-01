package entity;

import java.util.List;

/**
 * Entity representing a universe of securities (tickers) for backtesting.
 */
public class Universe {
    private final List<String> tickers;

    /**
     * Creates a Universe with the given list of tickers.
     * @param tickers list of ticker symbols
     * @throws IllegalArgumentException if tickers is null or empty
     */
    public Universe(List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) {
            throw new IllegalArgumentException("Universe must contain at least one ticker");
        }
        this.tickers = tickers;
    }

    public List<String> getTickers() {
        return tickers;
    }
}

