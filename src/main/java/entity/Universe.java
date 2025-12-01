package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a universe of securities (tickers) used in a backtest.
 */
public class Universe {
    private final List<String> tickers;

    /**
     * Creates a new Universe with the given tickers.
     * @param tickers the list of stock tickers
     */
    public Universe(List<String> tickers) {
        if (tickers == null) {
            throw new IllegalArgumentException("Tickers list cannot be null");
        }
        this.tickers = new ArrayList<>(tickers); // Defensive copy
    }

    /**
     * Returns a copy of the tickers list.
     * @return the list of tickers
     */
    public List<String> getTickers() {
        return new ArrayList<>(tickers);
    }

    /**
     * Gets the number of tickers in this universe.
     * @return the size of the universe
     */
    public int size() {
        return tickers.size();
    }

    /**
     * Checks if a ticker exists in this universe.
     * @param ticker the ticker to check
     * @return true if the ticker exists, false otherwise
     */
    public boolean contains(String ticker) {
        return tickers.contains(ticker);
    }
}


