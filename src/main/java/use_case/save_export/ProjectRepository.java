package use_case.save_export;

import entity.Project;

/**
 * Interface for persisting and loading Project entities.
 * This follows Clean Architecture by keeping the use case layer independent
 * of the specific storage implementation (file, database, etc.).
 */
public interface ProjectRepository {
    /**
     * Loads a project by its ID.
     * @param id the unique identifier of the project
     * @return the Project if found, null otherwise
     * @throws RuntimeException if there's an error accessing the repository
     */
    Project load(String id);

    /**
     * Saves a project to persistent storage.
     * If a project with the same ID already exists, it will be overwritten.
     * @param project the Project to save
     * @throws RuntimeException if there's an error saving (e.g., permission error, disk full)
     */
    void save(Project project);

    /**
     * Checks if a project with the given ID exists.
     * @param id the unique identifier of the project
     * @return true if the project exists, false otherwise
     */
    boolean exists(String id);
}

