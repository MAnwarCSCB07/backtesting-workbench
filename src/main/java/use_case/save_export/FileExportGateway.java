package use_case.save_export;

import entity.Project;

/**
 * Interface for exporting Project data to various file formats.
 * This follows Clean Architecture by keeping the use case layer independent
 * of the specific file system implementation.
 */
public interface FileExportGateway {
    /**
     * Exports the project data to a CSV file.
     * The CSV should include:
     * - Project metadata (name, ID, universe)
     * - Backtest configuration
     * - Factor scores (if available)
     * - Backtest results (equity curve, metrics, etc.)
     *
     * @param project the Project to export
     * @param filePath the path where the CSV file should be saved
     * @return the absolute path of the created CSV file
     * @throws RuntimeException if there's an error creating the file (e.g., permission error, invalid path)
     */
    String exportCSV(Project project, String filePath);

    /**
     * Exports the project data to an HTML file.
     * The HTML should be a formatted, shareable report including:
     * - Project metadata
     * - Backtest configuration
     * - Factor rankings table
     * - Performance metrics and charts (if results exist)
     *
     * @param project the Project to export
     * @param filePath the path where the HTML file should be saved
     * @return the absolute path of the created HTML file
     * @throws RuntimeException if there's an error creating the file (e.g., permission error, invalid path)
     */
    String exportHTML(Project project, String filePath);

    /**
     * Exports the project to JSON format for saving project state.
     * This allows the project to be loaded later with all its configuration and results.
     *
     * @param project the Project to export
     * @param filePath the path where the JSON file should be saved
     * @return the absolute path of the created JSON file
     * @throws RuntimeException if there's an error creating the file (e.g., permission error, invalid path)
     */
    String exportJSON(Project project, String filePath);
}


