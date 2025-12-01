package use_case.save_export;

import entity.BacktestConfig;
import entity.BacktestResult;
import entity.Project;
import entity.Universe;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for SaveExportInteractor with 100% line coverage.
 */
class SaveExportInteractorTest {

    /**
     * Creates a test project with all data populated.
     */
    private Project createTestProject() {
        List<String> tickers = Arrays.asList("AAPL", "GOOGL", "MSFT");
        Universe universe = new Universe(tickers);

        Map<String, Double> factorWeights = new HashMap<>();
        factorWeights.put("momentum", 0.5);
        factorWeights.put("value", 0.3);
        factorWeights.put("low_vol", 0.2);
        BacktestConfig config = new BacktestConfig("monthly", 10.0, 0.1, factorWeights);

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("Sharpe Ratio", 1.5);
        metrics.put("CAGR", 0.12);
        metrics.put("Max Drawdown", -0.15);
        List<Double> equityCurve = Arrays.asList(100.0, 105.0, 110.0, 108.0, 112.0);
        List<Double> drawdown = Arrays.asList(0.0, -0.05, -0.10, -0.12, -0.15);
        BacktestResult result = new BacktestResult(equityCurve, drawdown, metrics);

        return new Project("test-project-1", "Test Project", universe, config, result);
    }

    @Test
    void testSuccessCSVExport() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();

        // Mock repository
        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                if (id.equals(projectId)) {
                    return testProject;
                }
                return null;
            }

            @Override
            public void save(Project project) {
                // Mock save - do nothing
            }

            @Override
            public boolean exists(String id) {
                return id.equals(projectId);
            }
        };

        // Mock export gateway
        final List<String> exportedFiles = new ArrayList<>();
        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                exportedFiles.add(filePath);
                return filePath;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("HTML export should not be called for CSV export");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("JSON export should not be called for CSV export");
                return null;
            }
        };

        // Mock presenter
        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                assertTrue(outputData.isSuccess());
                assertNotNull(outputData.getMessage());
                assertEquals(1, outputData.getExportedFilePaths().size());
                assertTrue(outputData.getExportedFilePaths().get(0).contains(".csv"));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Success case should not call prepareFailView");
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "CSV");
        interactor.execute(inputData);
    }

    @Test
    void testSuccessHTMLExport() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return testProject;
            }

            @Override
            public void save(Project project) {
                // Mock save
            }

            @Override
            public boolean exists(String id) {
                return true;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                fail("CSV export should not be called for HTML export");
                return null;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                return filePath;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("JSON export should not be called for HTML export");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                assertTrue(outputData.isSuccess());
                assertEquals(1, outputData.getExportedFilePaths().size());
                assertTrue(outputData.getExportedFilePaths().get(0).contains(".html"));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Success case should not call prepareFailView");
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "HTML");
        interactor.execute(inputData);
    }

    @Test
    void testSuccessBOTHExport() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return testProject;
            }

            @Override
            public void save(Project project) {
                // Mock save
            }

            @Override
            public boolean exists(String id) {
                return true;
            }
        };

        final List<String> exportedFiles = new ArrayList<>();
        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                exportedFiles.add(filePath);
                return filePath;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                exportedFiles.add(filePath);
                return filePath;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("JSON export should not be called for BOTH export");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                assertTrue(outputData.isSuccess());
                assertEquals(2, outputData.getExportedFilePaths().size());
                assertTrue(outputData.getExportedFilePaths().get(0).contains(".csv"));
                assertTrue(outputData.getExportedFilePaths().get(1).contains(".html"));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Success case should not call prepareFailView");
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "BOTH");
        interactor.execute(inputData);
    }

    @Test
    void testSuccessJSONExport() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return testProject;
            }

            @Override
            public void save(Project project) {
                // Mock save
            }

            @Override
            public boolean exists(String id) {
                return true;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                fail("CSV export should not be called for JSON export");
                return null;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("HTML export should not be called for JSON export");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                return filePath;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                assertTrue(outputData.isSuccess());
                assertTrue(outputData.getMessage().contains("saved"));
                assertEquals(1, outputData.getExportedFilePaths().size());
                assertTrue(outputData.getExportedFilePaths().get(0).contains(".json"));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Success case should not call prepareFailView");
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "JSON");
        interactor.execute(inputData);
    }

    @Test
    void testFailureProjectNotFound() {
        String projectId = "non-existent-project";

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return null; // Project not found
            }

            @Override
            public void save(Project project) {
                fail("Save should not be called when project is not found");
            }

            @Override
            public boolean exists(String id) {
                return false;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                fail("Export should not be called when project is not found");
                return null;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("Export should not be called when project is not found");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("Export should not be called when project is not found");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                fail("Success should not be called when project is not found");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("not found"));
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "CSV");
        interactor.execute(inputData);
    }

    @Test
    void testFailureInvalidExportType() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return testProject;
            }

            @Override
            public void save(Project project) {
                // Mock save
            }

            @Override
            public boolean exists(String id) {
                return true;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                fail("Export should not be called for invalid export type");
                return null;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("Export should not be called for invalid export type");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("Export should not be called for invalid export type");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                fail("Success should not be called for invalid export type");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("Invalid export type"));
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "INVALID");
        interactor.execute(inputData);
    }

    @Test
    void testFailureRepositoryError() {
        String projectId = "test-project";

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                throw new RuntimeException("Database connection error");
            }

            @Override
            public void save(Project project) {
                // Not reached
            }

            @Override
            public boolean exists(String id) {
                return false;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                fail("Export should not be called when repository fails");
                return null;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("Export should not be called when repository fails");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("Export should not be called when repository fails");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                fail("Success should not be called when repository fails");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("Error during save/export"));
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "CSV");
        interactor.execute(inputData);
    }

    @Test
    void testFailureExportGatewayError() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return testProject;
            }

            @Override
            public void save(Project project) {
                // Mock save
            }

            @Override
            public boolean exists(String id) {
                return true;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                throw new RuntimeException("Permission denied: cannot write to file");
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("HTML should not be called for CSV export");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("JSON should not be called for CSV export");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                fail("Success should not be called when export fails");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("Error during save/export"));
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "CSV");
        interactor.execute(inputData);
    }

    @Test
    void testSuccessWithCustomFilePath() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();
        String customPath = "/custom/path/to/export";

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return testProject;
            }

            @Override
            public void save(Project project) {
                // Mock save
            }

            @Override
            public boolean exists(String id) {
                return true;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                assertEquals(customPath + ".csv", filePath);
                return filePath;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("HTML should not be called for CSV export");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("JSON should not be called for CSV export");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                assertTrue(outputData.isSuccess());
                assertEquals(1, outputData.getExportedFilePaths().size());
                assertEquals(customPath + ".csv", outputData.getExportedFilePaths().get(0));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Success case should not call prepareFailView");
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "CSV", customPath);
        interactor.execute(inputData);
    }

    @Test
    void testCaseInsensitiveExportType() {
        Project testProject = createTestProject();
        String projectId = testProject.getId();

        ProjectRepository mockRepository = new ProjectRepository() {
            @Override
            public Project load(String id) {
                return testProject;
            }

            @Override
            public void save(Project project) {
                // Mock save
            }

            @Override
            public boolean exists(String id) {
                return true;
            }
        };

        FileExportGateway mockGateway = new FileExportGateway() {
            @Override
            public String exportCSV(Project project, String filePath) {
                return filePath;
            }

            @Override
            public String exportHTML(Project project, String filePath) {
                fail("HTML should not be called for CSV export");
                return null;
            }

            @Override
            public String exportJSON(Project project, String filePath) {
                fail("JSON should not be called for CSV export");
                return null;
            }
        };

        SaveExportOutputBoundary mockPresenter = new SaveExportOutputBoundary() {
            @Override
            public void prepareSuccessView(SaveExportOutputData outputData) {
                assertTrue(outputData.isSuccess());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Lowercase export type should work");
            }
        };

        SaveExportInteractor interactor = new SaveExportInteractor(mockRepository, mockGateway, mockPresenter);
        SaveExportInputData inputData = new SaveExportInputData(projectId, "Test Project", "csv"); // lowercase
        interactor.execute(inputData);
    }
}

