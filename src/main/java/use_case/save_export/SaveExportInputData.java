package use_case.save_export;

/**
 * Input Data for the Save/Export Use Case.
 * Contains all the information needed to save and export a project.
 */
public class SaveExportInputData {
    private final String projectId;
    private final String projectName;
    private final String exportType; // "CSV", "HTML", "BOTH", or "JSON" for save only
    private final String filePath; // Optional: if null, use default location

    /**
     * Creates SaveExportInputData with all parameters.
     * @param projectId the ID of the project to save/export
     * @param projectName the name of the project
     * @param exportType the type of export: "CSV", "HTML", "BOTH", or "JSON"
     * @param filePath the path where files should be saved (null for default location)
     */
    public SaveExportInputData(String projectId, String projectName, String exportType, String filePath) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.exportType = exportType;
        this.filePath = filePath;
    }

    /**
     * Creates SaveExportInputData with default file path.
     * @param projectId the ID of the project to save/export
     * @param projectName the name of the project
     * @param exportType the type of export: "CSV", "HTML", "BOTH", or "JSON"
     */
    public SaveExportInputData(String projectId, String projectName, String exportType) {
        this(projectId, projectName, exportType, null);
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getExportType() {
        return exportType;
    }

    /**
     * Gets the file path, which may be null if default location should be used.
     * @return the file path, or null for default
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Checks if a custom file path was provided.
     * @return true if filePath is not null, false otherwise
     */
    public boolean hasCustomPath() {
        return filePath != null && !filePath.isEmpty();
    }
}


