package use_case.import_ohlcv;

import java.time.LocalDate;
import java.util.List;

/**
 * Immutable input data for the Import OHLCV use case.
 */
public class ImportOHLCVInputData {

    private final List<String> tickers;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String projectId;
    /**
     * Optional hint for the data source, e.g. "API" or "CSV".
     * The interactor can pass this through to the output for display.
     */
    private final String sourceType;

    public ImportOHLCVInputData(List<String> tickers,
                                LocalDate startDate,
                                LocalDate endDate,
                                String projectId,
                                String sourceType) {
        this.tickers = tickers;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectId = projectId;
        this.sourceType = sourceType;
    }

    public List<String> getTickers() {
        return tickers;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getSourceType() {
        return sourceType;
    }
}
