package interface_adapter.import_ohlcv;

import interface_adapter.ViewModel;

/**
 * ViewModel for UC-1: Import OHLCV.
 *
 * Holds ImportOHLCVState and notifies listeners (views)
 * whenever the state changes.
 */
public class ImportOHLCVViewModel extends ViewModel<ImportOHLCVState> {

    public static final String VIEW_NAME = "input stock data";

    public ImportOHLCVViewModel() {
        super(VIEW_NAME);
        // Initialize with a default, empty state
        this.setStateInternal(new ImportOHLCVState());
    }

    public ImportOHLCVState getState() {
        return getStateInternal();
    }

    public void setState(ImportOHLCVState state) {
        setStateInternal(state);
    }

    /**
     * Convenience method for presenters to fire updates.
     */
    public void fireStateChanged() {
        firePropertyChange();
    }

    // ---- Private helpers to avoid raw-type warnings ----

    private ImportOHLCVState getStateInternal() {
        return super.getState();
    }

    private void setStateInternal(ImportOHLCVState state) {
        super.setState(state);
    }
}
