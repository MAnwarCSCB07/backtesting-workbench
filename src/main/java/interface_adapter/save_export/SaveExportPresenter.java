package interface_adapter.save_export;

import use_case.save_export.SaveExportOutputBoundary;
import use_case.save_export.SaveExportOutputData;

/**
 * The Presenter for the Save/Export Use Case.
 * Formats the output data and updates the view model.
 */
public class SaveExportPresenter implements SaveExportOutputBoundary {
    private final SaveExportViewModel saveExportViewModel;

    /**
     * Creates a new SaveExportPresenter.
     * @param saveExportViewModel the view model to update
     */
    public SaveExportPresenter(SaveExportViewModel saveExportViewModel) {
        this.saveExportViewModel = saveExportViewModel;
    }

    @Override
    public void prepareSuccessView(SaveExportOutputData outputData) {
        SaveExportState state = saveExportViewModel.getState();
        
        // Build a detailed success message with file paths
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(outputData.getMessage());
        
        if (!outputData.getExportedFilePaths().isEmpty()) {
            messageBuilder.append("\n\nFiles saved to:");
            for (String path : outputData.getExportedFilePaths()) {
                messageBuilder.append("\n  - ").append(path);
            }
        }
        
        state.setMessage(messageBuilder.toString());
        state.setErrorMessage(null); // Clear any previous errors
        saveExportViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SaveExportState state = saveExportViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setMessage(null); // Clear any previous success message
        saveExportViewModel.firePropertyChange();
    }
}


