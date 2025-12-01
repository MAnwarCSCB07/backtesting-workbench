package entity;

import java.time.LocalDate;

/**
 * Represents a single trade executed during backtesting.
 */
public class Trade {
    private final LocalDate date;
    private final String symbol;
    private final Side side;
    private final double qty;
    private final double price;
    private final double costBps;

    /**
     * Creates a new Trade.
     * @param date the date of the trade
     * @param symbol the stock symbol
     * @param side whether it's a BUY or SELL
     * @param qty the quantity of shares
     * @param price the execution price per share
     * @param costBps the transaction cost in basis points
     */
    public Trade(LocalDate date, String symbol, Side side, double qty, double price, double costBps) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (side == null) {
            throw new IllegalArgumentException("Side cannot be null");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (costBps < 0) {
            throw new IllegalArgumentException("Cost in basis points cannot be negative");
        }
        this.date = date;
        this.symbol = symbol;
        this.side = side;
        this.qty = qty;
        this.price = price;
        this.costBps = costBps;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
    }

    public double getQty() {
        return qty;
    }

    public double getPrice() {
        return price;
    }

    public double getCostBps() {
        return costBps;
    }

    /**
     * Calculates the total value of this trade (quantity * price).
     * @return the total trade value
     */
    public double getTotalValue() {
        return qty * price;
    }

    /**
     * Calculates the transaction cost for this trade.
     * @return the transaction cost in dollars
     */
    public double getTransactionCost() {
        return getTotalValue() * (costBps / 10000.0);
    }
}

