package interface_adapter.factor_config;

import interface_adapter.ViewManagerModel;
import use_case.factor_config.FactorConfigOutputBoundary;
import use_case.factor_config.FactorConfigOutputData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Presenter for UC-2. Maps OutputData to FactorViewModel state.
 */
public class FactorConfigPresenter implements FactorConfigOutputBoundary {

    private final FactorViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public FactorConfigPresenter(FactorViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void present(FactorConfigOutputData outputData) {
        final List<FactorViewState.RowVM> rows = outputData.getRanked().stream()
                .map(r -> new FactorViewState.RowVM(r.symbol, r.composite, r.zScoresByFactor))
                .collect(Collectors.toList());

        final FactorViewState state = viewModel.getState();
        state.setRanked(rows);
        state.setError(null);
        viewModel.firePropertyChange();

        // Optional: switch to results view if present
        if (viewManagerModel != null) {
            viewManagerModel.setState(viewModel.getViewName());
            viewManagerModel.firePropertyChange();
        }
    }
}
