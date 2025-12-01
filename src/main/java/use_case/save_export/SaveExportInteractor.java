package use_case.save_export;

import entity.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * The Save/Export Interactor.
 * This is the core business logic for saving and exporting backtesting projects.
 * It coordinates between the ProjectRepository and FileExportGateway to:
 * 1. Load the project from storage
 * 2. Save the project state (persist any updates)
 * 3. Export the project to requested formats (CSV, HTML, or both)
 */
public class SaveExportInteractor implements SaveExportInputBoundary {
    private final ProjectRepository projectRepository;
    private final FileExportGateway fileExportGateway;
    private final SaveExportOutputBoundary presenter;

    /**
     * Creates a new SaveExportInteractor.
     * @param projectRepository the repository for loading/saving projects
     * @param fileExportGateway the gateway for exporting to files
     * @param presenter the output boundary for communicating results
     */
    public SaveExportInteractor(ProjectRepository projectRepository,
                                FileExportGateway fileExportGateway,
                                SaveExportOutputBoundary presenter) {
        this.projectRepository = projectRepository;
        this.fileExportGateway = fileExportGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(SaveExportInputData inputData) {
        try {
            // Step 1: Load the project from repository
            Project project = projectRepository.load(inputData.getProjectId());

            // If project doesn't exist, this is an error
            if (project == null) {
                presenter.prepareFailView("Project with ID \"" + inputData.getProjectId() + "\" not found.");
                return;
            }

            // Step 2: Save the project to persist any updates
            // This ensures the project state is saved before exporting
            projectRepository.save(project);

            // Step 3: Export based on the requested format
            List<String> exportedFiles = new ArrayList<>();
            String exportType = inputData.getExportType().toUpperCase();

            // Determine the base file path
            String basePath = inputData.hasCustomPath() 
                ? inputData.getFilePath() 
                : generateDefaultPath(inputData.getProjectName(), exportType);

            // Export based on type
            if ("CSV".equals(exportType)) {
                String csvPath = fileExportGateway.exportCSV(project, basePath + ".csv");
                exportedFiles.add(csvPath);
            } else if ("HTML".equals(exportType)) {
                String htmlPath = fileExportGateway.exportHTML(project, basePath + ".html");
                exportedFiles.add(htmlPath);
            } else if ("BOTH".equals(exportType)) {
                String csvPath = fileExportGateway.exportCSV(project, basePath + ".csv");
                String htmlPath = fileExportGateway.exportHTML(project, basePath + ".html");
                exportedFiles.add(csvPath);
                exportedFiles.add(htmlPath);
            } else if ("JSON".equals(exportType)) {
                // JSON is for saving project state only
                String jsonPath = fileExportGateway.exportJSON(project, basePath + ".json");
                exportedFiles.add(jsonPath);
            } else {
                presenter.prepareFailView("Invalid export type: " + exportType + ". Must be CSV, HTML, BOTH, or JSON.");
                return;
            }

            // Step 4: Prepare success view with results
            String successMessage = buildSuccessMessage(exportType, exportedFiles.size());
            SaveExportOutputData outputData = new SaveExportOutputData(successMessage, exportedFiles);
            presenter.prepareSuccessView(outputData);

        } catch (RuntimeException e) {
            // Handle any errors from repository or export gateway
            presenter.prepareFailView("Error during save/export: " + e.getMessage());
        }
    }

    /**
     * Generates a default file path based on project name and export type.
     * @param projectName the name of the project
     * @param exportType the type of export
     * @return the default file path (without extension)
     */
    private String generateDefaultPath(String projectName, String exportType) {
        // Sanitize project name for use in file path
        String sanitizedName = projectName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return "exports/" + sanitizedName + "_" + exportType.toLowerCase();
    }

    /**
     * Builds a success message based on export type and number of files.
     * @param exportType the type of export performed
     * @param fileCount the number of files exported
     * @return the success message
     */
    private String buildSuccessMessage(String exportType, int fileCount) {
        if ("JSON".equals(exportType)) {
            return "Project saved successfully!";
        } else if (fileCount == 1) {
            return "Project exported to " + exportType + " successfully!";
        } else {
            return "Project exported to " + fileCount + " files successfully!";
        }
    }
}


