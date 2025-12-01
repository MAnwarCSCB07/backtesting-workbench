package use_case.import_ohlcv;

import entity.PriceBar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository boundary for UC-1: project-level storage of imported prices.
 *
 * The implementation can:
 *  - just keep data in memory (for now),
 *  - or later be backed by a proper Project / Backtest repository.
 */
public interface ImportOHLCVProjectRepository {

    /**
     * Check whether a project (by ID) exists.
     */
    boolean projectExists(String projectId);

    /**
     * Persist imported prices for this project and date range.
     */
    void saveImportedPrices(
            String projectId,
            Map<String, List<PriceBar>> pricesByTicker,
            LocalDate start,
            LocalDate end
    );
}
