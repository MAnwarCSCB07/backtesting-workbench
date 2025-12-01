package entity;

/**
 * Entity representing a complete backtesting project.
 * Contains all project information including configuration and results.
 */
public class Project {
    private final String id;
    private final String name;
    private final Universe universe;
    private final BacktestConfig config;
    private final BacktestResult result; // Can be null if backtest hasn't been run yet

    /**
     * Creates a Project with the given parameters.
     * @param id unique project identifier
     * @param name project name
     * @param universe universe of securities
     * @param config backtest configuration
     * @param result backtest results (can be null)
     * @throws IllegalArgumentException if id, name, universe, or config are null/empty
     */
    public Project(String id, String name, Universe universe, BacktestConfig config, BacktestResult result) {
        if (id == null || "".equals(id.trim())) {
            throw new IllegalArgumentException("Project ID cannot be empty");
        }
        if (name == null || "".equals(name.trim())) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (universe == null) {
            throw new IllegalArgumentException("Universe cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("BacktestConfig cannot be null");
        }
        this.id = id;
        this.name = name;
        this.universe = universe;
        this.config = config;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Universe getUniverse() {
        return universe;
    }

    public BacktestConfig getConfig() {
        return config;
    }

    public BacktestResult getResult() {
        return result;
    }

    /**
     * Checks if the project has backtest results.
     * @return true if result is not null, false otherwise
     */
    public boolean hasResult() {
        return result != null;
    }
}

