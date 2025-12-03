package use_case.save_export;

/**
 * Input Boundary for the Save/Export Use Case.
 * This interface defines the contract for executing the save/export operation.
 */
public interface SaveExportInputBoundary {
    /**
     * Executes the Save/Export use case.
     * This will:
     * 1. Load or create the project
     * 2. Save the project state (if requested)
     * 3. Export to the requested format(s) (CSV, HTML, or both)
     * 4. Return results via the output boundary
     *
     * @param inputData the input data containing project info and export preferences
     */
    void execute(SaveExportInputData inputData);
}



