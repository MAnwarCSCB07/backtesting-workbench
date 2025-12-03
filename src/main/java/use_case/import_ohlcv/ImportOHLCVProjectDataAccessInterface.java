package use_case.import_ohlcv;

import entity.PriceBar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Gateway for attaching imported prices to a project and persisting it.
 *
 * This keeps the interactor independent of the actual Project storage details.
 */
public interface ImportOHLCVProjectDataAccessInterface {

    boolean projectExists(String projectId);

    void saveImportedPrices(
            String projectId,
            Map<String, List<PriceBar>> pricesByTicker,
            LocalDate start,
            LocalDate end
    );

    /**
     * @param projectId ID of the project that should store the imported prices.
     * @return true if a project with this ID exists, false otherwise.
     */
    boolean existsById(String projectId);

    /**
     * Save the imported price series into the specified project.
     * The implementation is responsible for loading the Project entity,
     * updating its universe / price storage, and persisting it.
     *
     * @param projectId  the target project ID.
     * @param priceSeries map from ticker symbol to its PriceBar series.
     * @throws RuntimeException (or a custom exception) if persistence fails.
     */
    void saveImportedPrices(String projectId,
                            Map<String, List<PriceBar>> priceSeries);
}
