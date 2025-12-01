package data_access;

import entity.BacktestConfig;
import entity.BacktestResult;
import entity.Project;
import entity.Universe;
import use_case.save_export.FileExportGateway;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
                writer.append("Project ID,").append(project.getId()).append("\n");
                writer.append("Project Name,").append(project.getName()).append("\n");
                writer.append("\n");

                // Write universe
                writer.append("Universe\n");
                Universe universe = project.getUniverse();
                for (String ticker : universe.getTickers()) {
                    writer.append(ticker).append("\n");
                }
                writer.append("\n");

                // Write backtest config
                BacktestConfig config = project.getConfig();
                writer.append("Backtest Configuration\n");
                writer.append("Rebalance Frequency,").append(config.getRebalanceFreq()).append("\n");
                writer.append("Transaction Cost (bps),").append(String.valueOf(config.getTransactionCost())).append("\n");
                writer.append("Position Cap,").append(String.valueOf(config.getPositionCap())).append("\n");
                writer.append("\n");

                // Write factor weights
                writer.append("Factor Weights\n");
                Map<String, Double> factorWeights = config.getFactorWeights();
                for (Map.Entry<String, Double> entry : factorWeights.entrySet()) {
                    writer.append(entry.getKey()).append(",").append(String.valueOf(entry.getValue())).append("\n");
                }
                writer.append("\n");

                // Write backtest results if available
                if (project.hasResults()) {
                    BacktestResult result = project.getResult();
                    writer.append("Backtest Results\n");
                    
                    // Write metrics
                    Map<String, Double> metrics = result.getMetrics();
                    for (Map.Entry<String, Double> entry : metrics.entrySet()) {
                        writer.append(entry.getKey()).append(",").append(String.valueOf(entry.getValue())).append("\n");
                    }
                    writer.append("\n");

                    // Write equity curve
                    writer.append("Equity Curve\n");
                    List<Double> equityCurve = result.getEquityCurve();
                    for (int i = 0; i < equityCurve.size(); i++) {
                        writer.append("Day ").append(String.valueOf(i + 1)).append(",")
                              .append(String.valueOf(equityCurve.get(i))).append("\n");
                    }
                }
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
                writer.append("<p><strong>Project ID:</strong> ").append(project.getId()).append("</p>\n");

                // Universe
                writer.append("<h2>Universe</h2>\n");
                writer.append("<ul>\n");
                Universe universe = project.getUniverse();
                for (String ticker : universe.getTickers()) {
                    writer.append("<li>").append(ticker).append("</li>\n");
                }
                writer.append("</ul>\n");

                // Backtest Configuration
                BacktestConfig config = project.getConfig();
                writer.append("<h2>Backtest Configuration</h2>\n");
                writer.append("<table>\n");
                writer.append("<tr><th>Parameter</th><th>Value</th></tr>\n");
                writer.append("<tr><td>Rebalance Frequency</td><td>").append(config.getRebalanceFreq()).append("</td></tr>\n");
                writer.append("<tr><td>Transaction Cost (bps)</td><td>").append(String.valueOf(config.getTransactionCost())).append("</td></tr>\n");
                writer.append("<tr><td>Position Cap</td><td>").append(String.valueOf(config.getPositionCap())).append("</td></tr>\n");
                writer.append("</table>\n");

                // Factor Weights
                writer.append("<h2>Factor Weights</h2>\n");
                writer.append("<table>\n");
                writer.append("<tr><th>Factor</th><th>Weight</th></tr>\n");
                Map<String, Double> factorWeights = config.getFactorWeights();
                for (Map.Entry<String, Double> entry : factorWeights.entrySet()) {
                    writer.append("<tr><td>").append(entry.getKey()).append("</td><td>")
                          .append(String.valueOf(entry.getValue())).append("</td></tr>\n");
                }
                writer.append("</table>\n");

                // Backtest Results
                if (project.hasResults()) {
                    BacktestResult result = project.getResult();
                    writer.append("<h2>Backtest Results</h2>\n");

                    // Metrics
                    writer.append("<h3>Performance Metrics</h3>\n");
                    writer.append("<table>\n");
                    writer.append("<tr><th>Metric</th><th>Value</th></tr>\n");
                    Map<String, Double> metrics = result.getMetrics();
                    for (Map.Entry<String, Double> entry : metrics.entrySet()) {
                        writer.append("<tr><td>").append(entry.getKey()).append("</td><td>")
                              .append(String.valueOf(entry.getValue())).append("</td></tr>\n");
                    }
                    writer.append("</table>\n");
                } else {
                    writer.append("<p><em>No backtest results available. Run a backtest first.</em></p>\n");
                }

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
                writer.append("  \"id\": \"").append(project.getId()).append("\",\n");
                writer.append("  \"name\": \"").append(project.getName()).append("\",\n");
                writer.append("  \"universe\": [\n");
                Universe universe = project.getUniverse();
                List<String> tickers = universe.getTickers();
                for (int i = 0; i < tickers.size(); i++) {
                    writer.append("    \"").append(tickers.get(i)).append("\"");
                    if (i < tickers.size() - 1) {
                        writer.append(",");
                    }
                    writer.append("\n");
                }
                writer.append("  ],\n");
                writer.append("  \"config\": {\n");
                BacktestConfig config = project.getConfig();
                writer.append("    \"rebalanceFreq\": \"").append(config.getRebalanceFreq()).append("\",\n");
                writer.append("    \"transactionCost\": ").append(String.valueOf(config.getTransactionCost())).append(",\n");
                writer.append("    \"positionCap\": ").append(String.valueOf(config.getPositionCap())).append(",\n");
                writer.append("    \"factorWeights\": {\n");
                Map<String, Double> factorWeights = config.getFactorWeights();
                int i = 0;
                for (Map.Entry<String, Double> entry : factorWeights.entrySet()) {
                    writer.append("      \"").append(entry.getKey()).append("\": ").append(String.valueOf(entry.getValue()));
                    if (i < factorWeights.size() - 1) {
                        writer.append(",");
                    }
                    writer.append("\n");
                    i++;
                }
                writer.append("    }\n");
                writer.append("  }");
                if (project.hasResults()) {
                    writer.append(",\n");
                    writer.append("  \"result\": {\n");
                    BacktestResult result = project.getResult();
                    writer.append("    \"metrics\": {\n");
                    Map<String, Double> metrics = result.getMetrics();
                    int j = 0;
                    for (Map.Entry<String, Double> entry : metrics.entrySet()) {
                        writer.append("      \"").append(entry.getKey()).append("\": ").append(String.valueOf(entry.getValue()));
                        if (j < metrics.size() - 1) {
                            writer.append(",");
                        }
                        writer.append("\n");
                        j++;
                    }
                    writer.append("    }\n");
                    writer.append("  }");
                }
                writer.append("\n}\n");
            }

            return Paths.get(filePath).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting to JSON: " + e.getMessage(), e);
        }
    }
}

