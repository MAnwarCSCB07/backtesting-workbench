package use_case.save_export;

/**
 * Input Boundary for the Save/Export use case.
 * Defines the interface for executing the save/export operation.
 */
public interface SaveExportInputBoundary {
    /**
     * Executes the save/export use case.
     * @param inputData the input data containing project ID, export type, and save location
     */
    void execute(SaveExportInputData inputData);
}

