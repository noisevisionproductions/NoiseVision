package org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.noisevisionproductions.noisevision.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.exceptions.ProjectNotFoundException;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.noisevisionproductions.noisevision.projectsManagement.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private static final String LOG_INIT_ERROR = "Error initializing collections for project {}: {}";

    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;
    private final ProjectCacheService projectCacheService;
    private final ProjectMapper projectMapper;

    public Project createProject(ProjectDTO projectDTO) {
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);

        if (project.getId() == null) {
            throw new RuntimeException("Project ID was not generated");
        }

        initializeProjectCollections(project, "new:");

        projectCacheService.cache(project.getId(), project);
        projectCacheService.invalidateProjectsList();

        return project;
    }

    public Project updateProject(Long id, ProjectDTO projectDTO) {
        Project existingProject = getProjectById(id);
        projectMapper.updateProjectFromDTO(existingProject, projectDTO);

        Project updatedProject = projectRepository.save(existingProject);
        projectCacheService.cache(id, updatedProject);
        projectCacheService.invalidateProjectsList();

        return updatedProject;
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        List<Project> cachedProjects = projectCacheService.getCachedProjectsList();
        if (cachedProjects != null && !cachedProjects.isEmpty()) {
            log.info("Returning {} projects from cache.", cachedProjects.size());
            return cachedProjects;
        }

        List<Project> projects = projectRepository.findAll();

        projects.forEach(project -> initializeProjectCollections(project, "list:"));

        if (!projects.isEmpty()) {
            log.info("Caching {} projects", projects.size());
            projectCacheService.cacheProjectsList(projects);
        }
        return projects;
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        Project cachedProject = projectCacheService.get(id);
        if (cachedProject != null) {
            initializeProjectCollections(cachedProject, "cached");
            return cachedProject;
        }

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        initializeProjectCollections(project, "id:");
        projectCacheService.cache(id, project);
        log.info("Project with ID {} has been cached.", id);

        return project;
    }

    @Transactional(readOnly = true)
    public Project getProjectBySlug(String slug) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Project not found with slug: " + slug));

        initializeProjectCollections(project, "slug:");

        Long projectId = project.getId();
        Project cachedProject = projectCacheService.get(projectId);
        if (cachedProject != null) {
            log.info("Returning project with slug '{}' (ID {}) from cache.", slug, projectId);
            return cachedProject;
        }

        projectCacheService.cache(projectId, project);
        return project;
    }

    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        if (project == null) {
            throw new ProjectNotFoundException("Project not found with id: " + id);
        }

        try {
            for (ImageFromProject image : project.getProjectImages()) {
                fileStorageService.deleteFile(image.getImageUrl());
            }

            project.getProjectImages().clear();
            project.getContributors().clear();
            project.getFeatures().clear();
            project.getTechnologies().clear();

            projectRepository.delete(project);

            try {
                projectCacheService.invalidate(id);
                projectCacheService.invalidateProjectsList();
            } catch (Exception e) {
                log.error("Failed to invalidate cache for project: {}", id, e);
            }

        } catch (Exception e) {
            log.error("Failed to delete project: {}", id, e);
            throw new RuntimeException("Failed to delete project and its resources", e);
        }
    }

    public Project updateFeatures(Long projectId, List<String> features) {
        Project project = getProjectById(projectId);
        project.setFeatures(features);
        Project updatedProject = projectRepository.save(project);

        projectCacheService.cache(projectId, updatedProject);
        projectCacheService.invalidateProjectsList();

        return updatedProject;
    }

    private void initializeProjectCollections(Project project, String context) {
        try {
            Hibernate.initialize(project.getProjectImages());
            Hibernate.initialize(project.getContributors());
            Hibernate.initialize(project.getFeatures());
            Hibernate.initialize(project.getTechnologies());
        } catch (Exception e) {
            String identifier = context + (project.getId() != null ? project.getId() : project.getSlug());
            log.error(LOG_INIT_ERROR, identifier, e.getMessage());
            throw new RuntimeException("Failed to initialize project collections: " + identifier, e);
        }
    }
}