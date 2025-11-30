package entity;


/**
 * A simple entity representing a project.
 * Projects have a name, an id, and a backtest configuration.
 */
public class Project {

    private final String name;
    private final String id;
    //private final Universe universe;
    private final BacktestConfig config;
    //private BacktestResult result;


    /**
     * Creates a new project with the given non-empty name and non-empty id, and the specified backtest configuration.
     *
     * @param id     the unique identifier for the project
     * @param name   the name of the project
     * @param config the backtest configuration
     * @throws IllegalArgumentException if the name or id are empty
     */
    public Project(String id, String name,/* Universe universe,*/ BacktestConfig config) {


        if (name.length() == 0) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (id.length() == 0) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        this.id = id;
        this.name = name;
        //this.universe = universe;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public BacktestConfig getConfig() {
        return config;
    }

}
