package use_case.save_export;

import java.util.ArrayList;
import java.util.List;

/**
 * Output Data for the Save/Export Use Case.
 * Contains the results of the save/export operation.
 */
public class SaveExportOutputData {
    private final boolean success;
    private final String message;
    private final List<String> exportedFilePaths;
    private final String errorMessage;

    /**
     * Creates a successful SaveExportOutputData.
     * @param message the success message
     * @param exportedFilePaths the list of file paths where exports were saved
     */
    public SaveExportOutputData(String message, List<String> exportedFilePaths) {
        this.success = true;
        this.message = message;
        this.exportedFilePaths = new ArrayList<>(exportedFilePaths);
        this.errorMessage = null;
    }

    /**
     * Creates a failed SaveExportOutputData.
     * @param errorMessage the error message explaining the failure
     */
    public SaveExportOutputData(String errorMessage) {
        this.success = false;
        this.message = null;
        this.exportedFilePaths = new ArrayList<>();
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the success message, which is null if the operation failed.
     * @return the success message, or null
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the list of exported file paths.
     * @return a copy of the list of file paths
     */
    public List<String> getExportedFilePaths() {
        return new ArrayList<>(exportedFilePaths);
    }

    /**
     * Gets the error message, which is null if the operation succeeded.
     * @return the error message, or null
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}

