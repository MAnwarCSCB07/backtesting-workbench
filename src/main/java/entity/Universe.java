package entity;

import java.util.List;

public class Universe {

    private final List<String> tickers;

    public Universe(List<String> tickers) {
        this.tickers = tickers;
    }

    public List<String> getTickers() {
        return tickers;
    }
}
