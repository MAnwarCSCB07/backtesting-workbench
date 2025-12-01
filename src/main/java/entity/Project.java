package entity;

/**
 * Represents a complete backtesting project, containing all configuration,
 * data, and results.
 */
public class Project {
    private final String id;
    private final String name;
    private final Universe universe;
    private final BacktestConfig config;
    private final BacktestResult result;

    /**
     * Creates a new Project without results (before backtest is run).
     * @param id the unique identifier for the project
     * @param name the name of the project
     * @param universe the universe of securities
     * @param config the backtest configuration
     */
    public Project(String id, String name, Universe universe, BacktestConfig config) {
        this(id, name, universe, config, null);
    }

    /**
     * Creates a new Project with results (after backtest is run).
     * @param id the unique identifier for the project
     * @param name the name of the project
     * @param universe the universe of securities
     * @param config the backtest configuration
     * @param result the backtest results (can be null if backtest hasn't been run)
     */
    public Project(String id, String name, Universe universe, BacktestConfig config, BacktestResult result) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
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

    /**
     * Gets the backtest results, which may be null if the backtest hasn't been run yet.
     * @return the BacktestResult, or null if not available
     */
    public BacktestResult getResult() {
        return result;
    }

    /**
     * Checks if this project has backtest results.
     * @return true if results exist, false otherwise
     */
    public boolean hasResults() {
        return result != null;
    }
}


