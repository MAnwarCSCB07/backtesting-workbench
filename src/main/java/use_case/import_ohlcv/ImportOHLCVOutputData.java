package use_case.import_ohlcv;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Output data for the Import OHLCV use case.
 * Summarizes what was imported so the Presenter can format for the ViewModel.
 */
public class ImportOHLCVOutputData {

    private final List<String> tickersLoaded;
    private final List<String> missingTickers;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String projectId;
    private final String sourceType;

    public ImportOHLCVOutputData(List<String> tickersLoaded,
                                 List<String> missingTickers,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 String projectId,
                                 String sourceType) {
        this.tickersLoaded = tickersLoaded == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(tickersLoaded);
        this.missingTickers = missingTickers == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(missingTickers);
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectId = projectId;
        this.sourceType = sourceType;
    }

    public List<String> getTickersLoaded() {
        return tickersLoaded;
    }

    public List<String> getMissingTickers() {
        return missingTickers;
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
