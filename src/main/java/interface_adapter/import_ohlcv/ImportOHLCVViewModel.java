package interface_adapter.import_ohlcv;

import interface_adapter.ViewModel;

/**
 * ViewModel for UC-1: Import OHLCV.
 * The view name matches the InputStockDataView card key.
 */
public class ImportOHLCVViewModel extends ViewModel<ImportOHLCVViewState> {

    public static final String VIEW_NAME = "input stock data";

    public ImportOHLCVViewModel() {
        super(VIEW_NAME);
        this.setState(new ImportOHLCVViewState());
    }

    @Override
    public ImportOHLCVViewState getState() {
        return super.getState();
    }

    @Override
    public void setState(ImportOHLCVViewState state) {
        super.setState(state);
    }
}
