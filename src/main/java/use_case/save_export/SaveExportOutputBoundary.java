package use_case.save_export;

/**
 * Output Boundary for the Save/Export Use Case.
 * This interface defines how the use case communicates results back to the presenter.
 */
public interface SaveExportOutputBoundary {
    /**
     * Prepares the success view for the Save/Export Use Case.
     * Called when the save/export operation completes successfully.
     *
     * @param outputData the output data containing success message and file paths
     */
    void prepareSuccessView(SaveExportOutputData outputData);

    /**
     * Prepares the failure view for the Save/Export Use Case.
     * Called when the save/export operation fails.
     *
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}


