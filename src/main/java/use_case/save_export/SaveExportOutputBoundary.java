package use_case.save_export;

/**
 * Output Boundary for the Save/Export use case.
 * Defines the interface for presenting the results of the save/export operation.
 */
public interface SaveExportOutputBoundary {
    /**
     * Prepares the success view for the Save/Export use case.
     * @param outputData the output data containing file paths and success message
     */
    void prepareSuccessView(SaveExportOutputData outputData);

    /**
     * Prepares the failure view for the Save/Export use case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}

