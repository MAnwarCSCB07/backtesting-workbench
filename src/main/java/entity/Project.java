package entity;


/**
 * A simple entity representing a user. Users have a username and password.
 */
public class Project {

    private final String name;
    private final String id;
    //private final Universe universe;
    private final BacktestConfig config;
    //private BacktestResult result;


    /**
     * Creates a new user with the given non-empty name and non-empty password.
     *
     * @param name   the username
     * @param id     //@param universe
     * @param config the backtest configuration
     * @throws IllegalArgumentException if the password or name are empty
     */
    public Project(String id, String name,/* Universe universe,*/ BacktestConfig config) {


        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(id)) {
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
