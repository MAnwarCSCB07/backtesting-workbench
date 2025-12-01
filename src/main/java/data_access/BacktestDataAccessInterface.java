package data_access;

import entity.BacktestConfig;
import entity.Universe;
import entity.PriceBar;
import entity.BacktestResult;

import java.time.LocalDate;
import java.util.List;

public interface BacktestDataAccessInterface {

    BacktestConfig getConfig(String projectId);

    Universe getUniverse(String projectId);

    List<PriceBar> getPriceSeries(String ticker, LocalDate start, LocalDate end);

    void saveResult(BacktestResult result);
}