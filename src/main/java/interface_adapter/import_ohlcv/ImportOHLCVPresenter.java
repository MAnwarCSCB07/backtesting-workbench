package interface_adapter.import_ohlcv;

import interface_adapter.ViewManagerModel;
import use_case.import_ohlcv.ImportOHLCVOutputBoundary;
import use_case.import_ohlcv.ImportOHLCVOutputData;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Presenter for UC-1. Maps ImportOHLCVOutputData to ImportOHLCVViewModel state.
 */
public class ImportOHLCVPresenter implements ImportOHLCVOutputBoundary {

    private final ImportOHLCVViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * @param viewModel        Import view model to update.
     * @param viewManagerModel View manager for navigation (can be null if not needed).
     */
    public ImportOHLCVPresenter(ImportOHLCVViewModel viewModel,
                                ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(ImportOHLCVOutputData outputData) {

        ImportOHLCVViewState state = viewModel.getState();

        state.setProjectId(outputData.getProjectId());

        List<String> loaded = safeList(outputData.getTickersLoaded());
        List<String> missing = safeList(outputData.getMissingTickers());

        state.setTickersLoaded(loaded);
        state.setMissingTickers(missing);

        String message;
        if (loaded.isEmpty()) {
            message = "No price data was loaded.";
        } else {
            String loadedStr = String.join(", ", loaded);
            if (!missing.isEmpty()) {
                String missingStr = String.join(", ", missing);
                message = String.format(
                        "Imported data for: %s. Missing: %s.",
                        loadedStr,
                        missingStr
                );
            } else {
                message = String.format("Imported data for: %s.", loadedStr);
            }
        }

        state.setStatusMessage(message);

        viewModel.setState(state);
        viewModel.firePropertyChange();

        // We generally stay on the same view ("input stock data"), so no navigation needed.
        // If you wanted to force showing this card:
        // if (viewManagerModel != null) {
        //     viewManagerModel.setState(viewModel.getViewName());
        //     viewManagerModel.firePropertyChange();
        // }
    }

    @Override
    public void prepareFailView(String errorMessage) {

        ImportOHLCVViewState state = viewModel.getState();
        state.setStatusMessage(errorMessage);
        state.setTickersLoaded(Collections.emptyList());
        state.setMissingTickers(Collections.emptyList());

        viewModel.setState(state);
        viewModel.firePropertyChange();

        // Again, we stay on the same view; no navigation change required.
    }

    private List<String> safeList(List<String> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        // Normalize whitespace just in case
        return list.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
