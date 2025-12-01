package interface_adapter.save_export;

/**
 * The state for the Save/Export View Model.
 */
public class SaveExportState {
    private String projectId = "";
    private String projectName = "";
    private String exportType = "BOTH"; // Default to exporting both CSV and HTML
    private String filePath = "";
    private String message = "";
    private String errorMessage = "";

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "SaveExportState{"
                + "projectId='" + projectId + '\''
                + ", projectName='" + projectName + '\''
                + ", exportType='" + exportType + '\''
                + ", filePath='" + filePath + '\''
                + ", message='" + message + '\''
                + ", errorMessage='" + errorMessage + '\''
                + '}';
    }
}


