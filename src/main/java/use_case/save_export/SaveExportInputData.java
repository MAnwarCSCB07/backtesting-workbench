package use_case.save_export;

/**
 * Input data for the Save/Export use case.
 * Contains the information needed to save and export a project.
 */
public class SaveExportInputData {
    private final String projectId;
    private final String exportFileType; // "CSV", "HTML", or "BOTH"
    private final String saveLocation; // Directory path, or null for default

    /**
     * Creates SaveExportInputData with the given parameters.
     * @param projectId the ID of the project to save/export
     * @param exportFileType the type of export file ("CSV", "HTML", or "BOTH")
     * @param saveLocation the directory to save files (null for default location)
     */
    public SaveExportInputData(String projectId, String exportFileType, String saveLocation) {
        this.projectId = projectId;
        this.exportFileType = exportFileType;
        this.saveLocation = saveLocation;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getExportFileType() {
        return exportFileType;
    }

    public String getSaveLocation() {
        return saveLocation;
    }
}

