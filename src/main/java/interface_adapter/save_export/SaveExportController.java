package interface_adapter.save_export;

import use_case.save_export.SaveExportInputBoundary;
import use_case.save_export.SaveExportInputData;

/**
 * Controller for the Save/Export Use Case.
 * Receives user input from the view and delegates to the interactor.
 */
public class SaveExportController {
    private final SaveExportInputBoundary saveExportInteractor;

    /**
     * Creates a new SaveExportController.
     * @param saveExportInteractor the interactor to delegate to
     */
    public SaveExportController(SaveExportInputBoundary saveExportInteractor) {
        this.saveExportInteractor = saveExportInteractor;
    }

    /**
     * Executes the Save/Export Use Case.
     * @param projectId the ID of the project to save/export
     * @param projectName the name of the project
     * @param exportType the type of export: "CSV", "HTML", "BOTH", or "JSON"
     * @param filePath the optional file path (null for default location)
     */
    public void execute(String projectId, String projectName, String exportType, String filePath) {
        SaveExportInputData inputData = new SaveExportInputData(projectId, projectName, exportType, filePath);
        saveExportInteractor.execute(inputData);
    }

    /**
     * Executes the Save/Export Use Case with default file path.
     * @param projectId the ID of the project to save/export
     * @param projectName the name of the project
     * @param exportType the type of export: "CSV", "HTML", "BOTH", or "JSON"
     */
    public void execute(String projectId, String projectName, String exportType) {
        execute(projectId, projectName, exportType, null);
    }
}


