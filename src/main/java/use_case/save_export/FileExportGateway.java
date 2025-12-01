package use_case.save_export;

import entity.Project;

/**
 * Interface for the File Export Gateway.
 * Defines methods for exporting Project data to various file formats.
 * This interface is implemented in the framework/data access layer.
 */
public interface FileExportGateway {
    /**
     * Exports the project data to a CSV file.
     * @param project the Project entity to export
     * @return the file path where the CSV was saved
     * @throws RuntimeException if there is an error exporting to CSV
     */
    String exportCSV(Project project);

    /**
     * Exports the project data to an HTML file.
     * @param project the Project entity to export
     * @return the file path where the HTML was saved
     * @throws RuntimeException if there is an error exporting to HTML
     */
    String exportHTML(Project project);
}

