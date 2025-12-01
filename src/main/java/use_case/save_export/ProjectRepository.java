package use_case.save_export;

import entity.Project;

/**
 * Interface for the Project Repository.
 * Defines methods for loading and saving Project entities.
 * This interface is implemented in the framework/data access layer.
 */
public interface ProjectRepository {
    /**
     * Loads a project by its ID.
     * @param id the project identifier
     * @return the Project entity, or null if not found
     * @throws RuntimeException if there is an error loading the project
     */
    Project load(String id);

    /**
     * Saves a project to persistent storage.
     * @param project the Project entity to save
     * @throws RuntimeException if there is an error saving the project
     */
    void save(Project project);
}

