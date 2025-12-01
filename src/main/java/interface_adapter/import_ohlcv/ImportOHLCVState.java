package interface_adapter.import_ohlcv;

import java.util.ArrayList;
import java.util.List;

/**
 * View-model state for the Import OHLCV screen.
 * Holds the result of the last import operation.
 *
 * This replaces the old ImportOHLCVViewState.
 */
public class ImportOHLCVState {

    /**
     * Optional: project ID for which data was last imported.
     */
    private String projectId;

    /**
     * Human-readable status message (success or error).
     */
    private String statusMessage;

    /**
     * Tickers for which price data was successfully loaded.
     */
    private List<String> tickersLoaded = new ArrayList<>();

    /**
     * Tickers that were requested but had no data.
     */
    private List<String> missingTickers = new ArrayList<>();

    // ---------- Core getters / setters ----------

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<String> getTickersLoaded() {
        return tickersLoaded;
    }

    public void setTickersLoaded(List<String> tickersLoaded) {
        this.tickersLoaded = tickersLoaded != null ? tickersLoaded : new ArrayList<>();
    }

    public List<String> getMissingTickers() {
        return missingTickers;
    }

    public void setMissingTickers(List<String> missingTickers) {
        this.missingTickers = missingTickers != null ? missingTickers : new ArrayList<>();
    }

    public List<String> getLoadedTickers() {
        return tickersLoaded;
    }

    public void setLoadedTickers(List<String> loadedTickers) {
        setTickersLoaded(loadedTickers);
    }
}
