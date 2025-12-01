package use_case.save_export;

import entity.Project;

/**
 * The Save/Export Interactor.
 * Implements the core business logic for saving and exporting projects.
 */
public class SaveExportInteractor implements SaveExportInputBoundary {
    private final ProjectRepository projectRepository;
    private final FileExportGateway fileExportGateway;
    private final SaveExportOutputBoundary saveExportPresenter;

    /**
     * Creates a SaveExportInteractor with the given dependencies.
     * @param projectRepository repository for loading and saving projects
     * @param fileExportGateway gateway for exporting to files
     * @param saveExportPresenter presenter for handling output
     */
    public SaveExportInteractor(ProjectRepository projectRepository,
                                FileExportGateway fileExportGateway,
                                SaveExportOutputBoundary saveExportPresenter) {
        this.projectRepository = projectRepository;
        this.fileExportGateway = fileExportGateway;
        this.saveExportPresenter = saveExportPresenter;
    }

    @Override
    public void execute(SaveExportInputData inputData) {
        // Load the project
        Project project = projectRepository.load(inputData.getProjectId());
        
        if (project == null) {
            saveExportPresenter.prepareFailView("Project not found: " + inputData.getProjectId());
            return;
        }

        // Save the project
        try {
            projectRepository.save(project);
        } catch (RuntimeException e) {
            saveExportPresenter.prepareFailView("Failed to save project: " + e.getMessage());
            return;
        }

        // Determine export type and perform exports
        String exportType = inputData.getExportFileType().toUpperCase();
        String csvFilePath = null;
        String htmlFilePath = null;

        try {
            if ("CSV".equals(exportType) || "BOTH".equals(exportType)) {
                csvFilePath = fileExportGateway.exportCSV(project);
            }

            if ("HTML".equals(exportType) || "BOTH".equals(exportType)) {
                htmlFilePath = fileExportGateway.exportHTML(project);
            }

            if (!"CSV".equals(exportType) && !"HTML".equals(exportType) && !"BOTH".equals(exportType)) {
                saveExportPresenter.prepareFailView("Invalid export type: " + exportType + ". Must be CSV, HTML, or BOTH");
                return;
            }

            // Build success message
            StringBuilder message = new StringBuilder("Project saved and exported successfully.");
            if (csvFilePath != null) {
                message.append(" CSV: ").append(csvFilePath);
            }
            if (htmlFilePath != null) {
                message.append(" HTML: ").append(htmlFilePath);
            }

            // Create output data
            String projectFilePath = "projects/" + project.getId() + ".json"; // Default path
            SaveExportOutputData outputData = new SaveExportOutputData(
                    message.toString(),
                    csvFilePath,
                    htmlFilePath,
                    projectFilePath
            );

            saveExportPresenter.prepareSuccessView(outputData);

        } catch (RuntimeException e) {
            saveExportPresenter.prepareFailView("Failed to export files: " + e.getMessage());
        }
    }
}

