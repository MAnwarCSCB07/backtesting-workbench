package entity;


/**
 * A simple entity representing a project.
 * Projects have a name, an projectId, and a backtest configuration.
 */
public class Project {

    private final String name;
    private final String projectId;
    private final BacktestConfig config;

    /**
     * Creates a new project with the given non-empty name and non-empty projectId, and the specified backtest configuration.
     *
     * @param id     the unique identifier for the project
     * @param name   the name of the project
     * @param config the backtest configuration
     * @throws IllegalArgumentException if the name or projectId are empty
     */
    public Project(String id, String name, BacktestConfig config) {
        if (name.length() == 0) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (id.length() == 0) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        this.projectId = id;
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public String getProjectId() {
        return projectId;
    }

    public BacktestConfig getConfig() {
        return config;
    }
}
