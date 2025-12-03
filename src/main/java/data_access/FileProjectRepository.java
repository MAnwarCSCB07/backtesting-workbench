package data_access;

import entity.Project;
import use_case.save_export.ProjectRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * File-based implementation of ProjectRepository.
 * Stores projects in memory (can be extended to persist to file later).
 */
public class FileProjectRepository implements ProjectRepository {
    private final Map<String, Project> projects = new HashMap<>();

    @Override
    public Project load(String id) {
        return projects.get(id);
    }

    @Override
    public void save(Project project) {
        projects.put(project.getProjectId(), project);
    }

    @Override
    public boolean exists(String id) {
        return projects.containsKey(id);
    }
}



