package data_access;

import entity.BacktestConfig;
import entity.Project;
import use_case.save_export.FileExportGateway;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of FileExportGateway for exporting projects to CSV, HTML, and JSON formats.
 */
public class FileExportGatewayImpl implements FileExportGateway {

    @Override
    public String exportCSV(Project project, String filePath) {
        try {
            // Ensure directory exists
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            try (FileWriter writer = new FileWriter(filePath)) {
                // Write project metadata
                writer.append("Project ID,").append(project.getProjectId()).append("\n");
                writer.append("Project Name,").append(project.getName()).append("\n");
                writer.append("\n");

                // Write backtest config
                BacktestConfig config = project.getConfig();
                writer.append("Backtest Configuration\n");
                writer.append("Project ID,").append(config.getProjectId()).append("\n");
                if (config.getStartDate() != null) {
                    writer.append("Start Date,").append(config.getStartDate().toString()).append("\n");
                }
                if (config.getEndDate() != null) {
                    writer.append("End Date,").append(config.getEndDate().toString()).append("\n");
                }
                writer.append("Initial Capital,").append(String.valueOf(config.getInitialCapital())).append("\n");
                writer.append("Strategy Name,").append(config.getStrategyName()).append("\n");
                writer.append("\n");
            }

            return Paths.get(filePath).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting to CSV: " + e.getMessage(), e);
        }
    }

    @Override
    public String exportHTML(Project project, String filePath) {
        try {
            // Ensure directory exists
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.append("<!DOCTYPE html>\n");
                writer.append("<html>\n<head>\n");
                writer.append("<title>Backtest Report: ").append(project.getName()).append("</title>\n");
                writer.append("<style>\n");
                writer.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
                writer.append("h1, h2 { color: #333; }\n");
                writer.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }\n");
                writer.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
                writer.append("th { background-color: #4CAF50; color: white; }\n");
                writer.append("tr:nth-child(even) { background-color: #f2f2f2; }\n");
                writer.append("</style>\n");
                writer.append("</head>\n<body>\n");

                // Project header
                writer.append("<h1>Backtest Report: ").append(project.getName()).append("</h1>\n");
                writer.append("<p><strong>Project ID:</strong> ").append(project.getProjectId()).append("</p>\n");

                // Backtest Configuration
                BacktestConfig config = project.getConfig();
                writer.append("<h2>Backtest Configuration</h2>\n");
                writer.append("<table>\n");
                writer.append("<tr><th>Parameter</th><th>Value</th></tr>\n");
                writer.append("<tr><td>Project ID</td><td>").append(config.getProjectId()).append("</td></tr>\n");
                if (config.getStartDate() != null) {
                    writer.append("<tr><td>Start Date</td><td>").append(config.getStartDate().toString()).append("</td></tr>\n");
                }
                if (config.getEndDate() != null) {
                    writer.append("<tr><td>End Date</td><td>").append(config.getEndDate().toString()).append("</td></tr>\n");
                }
                writer.append("<tr><td>Initial Capital</td><td>").append(String.valueOf(config.getInitialCapital())).append("</td></tr>\n");
                writer.append("<tr><td>Strategy Name</td><td>").append(config.getStrategyName()).append("</td></tr>\n");
                writer.append("</table>\n");

                writer.append("</body>\n</html>\n");
            }

            return Paths.get(filePath).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting to HTML: " + e.getMessage(), e);
        }
    }

    @Override
    public String exportJSON(Project project, String filePath) {
        try {
            // Ensure directory exists
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.append("{\n");
                writer.append("  \"id\": \"").append(project.getProjectId()).append("\",\n");
                writer.append("  \"name\": \"").append(project.getName()).append("\",\n");
                writer.append("  \"config\": {\n");
                BacktestConfig config = project.getConfig();
                writer.append("    \"projectId\": \"").append(config.getProjectId()).append("\",\n");
                if (config.getStartDate() != null) {
                    writer.append("    \"startDate\": \"").append(config.getStartDate().toString()).append("\",\n");
                }
                if (config.getEndDate() != null) {
                    writer.append("    \"endDate\": \"").append(config.getEndDate().toString()).append("\",\n");
                }
                writer.append("    \"initialCapital\": ").append(String.valueOf(config.getInitialCapital())).append(",\n");
                writer.append("    \"strategyName\": \"").append(config.getStrategyName()).append("\"\n");
                writer.append("  }\n");
                writer.append("}\n");
            }

            return Paths.get(filePath).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting to JSON: " + e.getMessage(), e);
        }
    }
}

