package use_case.save_export;

/**
 * Output data for the Save/Export use case.
 * Contains the results of the save/export operation.
 */
public class SaveExportOutputData {
    private final boolean success;
    private final String message;
    private final String csvFilePath; // Path to exported CSV file, or null
    private final String htmlFilePath; // Path to exported HTML file, or null
    private final String projectFilePath; // Path to saved project JSON file, or null
    private final String errorMessage; // Error message if operation failed

    /**
     * Creates SaveExportOutputData for a successful operation.
     * @param message success message
     * @param csvFilePath path to CSV file (null if not exported)
     * @param htmlFilePath path to HTML file (null if not exported)
     * @param projectFilePath path to saved project JSON file
     */
    public SaveExportOutputData(String message, String csvFilePath, String htmlFilePath, 
                                String projectFilePath) {
        this.success = true;
        this.message = message;
        this.csvFilePath = csvFilePath;
        this.htmlFilePath = htmlFilePath;
        this.projectFilePath = projectFilePath;
        this.errorMessage = null;
    }

    /**
     * Creates SaveExportOutputData for a failed operation.
     * @param errorMessage error message describing the failure
     */
    public SaveExportOutputData(String errorMessage) {
        this.success = false;
        this.message = null;
        this.csvFilePath = null;
        this.htmlFilePath = null;
        this.projectFilePath = null;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public String getHtmlFilePath() {
        return htmlFilePath;
    }

    public String getProjectFilePath() {
        return projectFilePath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

