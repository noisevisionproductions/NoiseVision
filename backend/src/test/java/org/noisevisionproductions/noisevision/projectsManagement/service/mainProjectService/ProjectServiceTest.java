package org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.noisevision.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.noisevisionproductions.noisevision.projectsManagement.service.FileStorageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ProjectCacheService projectCacheService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void getAllProjects_ShouldReturnCachedProjects_WhenCacheExists() {
        List<Project> cachedProjects = Arrays.asList(
                createTestProject(1L, "Test Project 1"),
                createTestProject(2L, "Test Project 2")
        );

        when(projectCacheService.getCachedProjectsList()).thenReturn(cachedProjects);

        List<Project> result = projectService.getAllProjects();

        assertThat(result).isEqualTo(cachedProjects);
        verify(projectCacheService).getCachedProjectsList();
        verify(projectRepository, never()).findAll();
    }

    @Test
    void getAllProjects_ShouldReturnFromDatabase_WhenCacheIsEmpty() {
        List<Project> databaseProjects = Arrays.asList(
                createTestProject(1L, "Test Project 1"),
                createTestProject(2L, "Test Project 2")
        );

        when(projectCacheService.getCachedProjectsList()).thenReturn(null);
        when(projectRepository.findAll()).thenReturn(databaseProjects);

        List<Project> result = projectService.getAllProjects();

        assertThat(result).isEqualTo(databaseProjects);
        verify(projectCacheService).getCachedProjectsList();
        verify(projectCacheService).cacheProjectsList(databaseProjects);
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectById_ShouldReturnCachedProject_WhenCacheExists() {
        Long projectId = 1L;
        Project cachedProject = new Project();
        cachedProject.setId(projectId);

        when(projectCacheService.get(projectId)).thenReturn(cachedProject);

        Project result = projectService.getProjectById(projectId);

        verify(projectCacheService).get(projectId);
        verify(projectRepository, never()).findById(projectId);
        assertThat(result).isEqualTo(cachedProject);
    }

    @Test
    void getProjectById_ShouldCacheAndReturnProject_WhenNotInCached() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);

        when(projectCacheService.get(projectId)).thenReturn(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(projectId);

        verify(projectCacheService).get(projectId);
        verify(projectRepository).findById(projectId);
        verify(projectCacheService).cache(projectId, project);
        assertThat(result).isEqualTo(project);
    }

    @Test
    void getProjectById_ShouldThrowException_WhenNotFound() {
        Long projectId = 1L;

        when(projectCacheService.get(projectId)).thenReturn(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(projectId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Project not found with id");
    }

    @Test
    void createProject_ShouldCreateNewProject_WhenValidDataProvided() {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Test Project");

        Project expectedProject = createTestProject(1L, "Test Project");

        when(projectMapper.toEntity(projectDTO)).thenReturn(expectedProject);
        when(projectRepository.save(any(Project.class))).thenReturn(expectedProject);

        Project result = projectService.createProject(projectDTO);

        verify(projectMapper).toEntity(projectDTO);
        verify(projectRepository).save(expectedProject);
        verify(projectCacheService).cache(expectedProject.getId(), expectedProject);
        verify(projectCacheService).invalidateProjectsList();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(projectDTO.getName());
    }

    @Test
    void createProject_ShouldInvalidateAllProjectsCache() {
        ProjectDTO projectDTO = new ProjectDTO();
        Project project = new Project();
        project.setId(1L);

        when(projectMapper.toEntity(projectDTO)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectService.createProject(projectDTO);

        verify(projectCacheService).cache(project.getId(), project);
        verify(projectCacheService).invalidateProjectsList();
    }

    @Test
    void updateProject_ShouldUpdateProject_WhenValidDataProvided() {
        Long projectId = 1L;
        ProjectDTO updateDTO = new ProjectDTO();
        Project existingProject = new Project();
        Project updatedProject = new Project();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(updatedProject);

        Project result = projectService.updateProject(projectId, updateDTO);

        verify(projectMapper).updateProjectFromDTO(existingProject, updateDTO);
        verify(projectRepository).save(existingProject);
        verify(projectCacheService).cache(projectId, updatedProject);
        assertThat(result).isEqualTo(updatedProject);
    }

    @Test
    void updateProject_ShouldInvalidateAllProjectsCache() {
        Long projectId = 1L;
        ProjectDTO projectDTO = new ProjectDTO();
        Project existingProject = new Project();
        Project updatedProject = new Project();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(updatedProject);

        projectService.updateProject(projectId, projectDTO);

        verify(projectCacheService).cache(projectId, updatedProject);
        verify(projectCacheService).invalidateProjectsList();
    }

    @Test
    void deleteProject_ShouldDeleteProjectAndImages() {
        Long projectId = 1L;
        Project project = new Project();

        ImageFromProject image1 = new ImageFromProject();
        image1.setImageUrl("/images/1.jpg");
        ImageFromProject image2 = new ImageFromProject();
        image2.setImageUrl("/images/2.jpg");

        project.setProjectImages(new ArrayList<>(Arrays.asList(image1, image2)));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.deleteProject(projectId);

        verify(fileStorageService).deleteFile("/images/1.jpg");
        verify(fileStorageService).deleteFile("/images/2.jpg");
        verify(projectRepository).delete(project);
        verify(projectCacheService).invalidate(projectId);
        verify(projectCacheService).invalidateProjectsList();

        assertThat(project.getProjectImages()).isEmpty();
        assertThat(project.getContributors()).isEmpty();
        assertThat(project.getFeatures()).isEmpty();
        assertThat(project.getTechnologies()).isEmpty();
    }

    @Test
    void deleteProject_ShouldInvalidateAllProjectsCache() {
        Long projectId = 1L;
        Project project = new Project();
        project.setProjectImages(new ArrayList<>());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.deleteProject(projectId);

        verify(projectCacheService).invalidate(projectId);
        verify(projectCacheService).invalidateProjectsList();
    }

    @Test
    void updateFeatures_ShouldUpdateProjectFeatures() {
        Long projectId = 1L;
        Project project = new Project();
        Project updatedProject = new Project();
        List<String> newFeatures = Arrays.asList("Feature 1", "Feature 2");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(updatedProject);

        Project result = projectService.updateFeatures(projectId, newFeatures);

        verify(projectRepository).save(project);
        verify(projectCacheService).cache(projectId, updatedProject);
        assertThat(result).isEqualTo(updatedProject);
    }

    @Test
    void updateFeatures_ShouldInvalidateAllProjectsCache() {
        Long projectId = 1L;
        Project project = new Project();
        Project updatedProject = new Project();
        List<String> newFeatures = Arrays.asList("Feature 1", "Feature 2");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(updatedProject);

        projectService.updateFeatures(projectId, newFeatures);

        verify(projectCacheService).cache(projectId, updatedProject);
        verify(projectCacheService).invalidateProjectsList();
    }

    @Test
    void getProjectBySlug_ShouldReturnCachedProject_WhenCacheExists() {
        String slug = "test-project";
        Project project = new Project();
        project.setId(1L);
        project.setSlug(slug);

        when(projectRepository.findBySlug(slug)).thenReturn(Optional.of(project));
        when(projectCacheService.get(project.getId())).thenReturn(project);

        Project result = projectService.getProjectBySlug(slug);

        verify(projectRepository).findBySlug(slug);
        verify(projectCacheService).get(project.getId());
        verify(projectCacheService, never()).cache(any(), any());
        assertThat(result).isEqualTo(project);
    }

    @Test
    void getProjectBySlug_ShouldReturnProject_WhenExists() {
        String slug = "test-project";
        Project project = new Project();
        project.setSlug(slug);

        when(projectRepository.findBySlug(slug)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectBySlug(slug);

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo(slug);
    }

    @Test
    void getProjectBySlug_ShouldThrownException_WhenNotFound() {
        String slug = "non-existent";
        when(projectRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectBySlug(slug))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Project not found with slug");
    }

    private Project createTestProject(Long id, String name) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setProjectImages(new ArrayList<>());
        project.setFeatures(new ArrayList<>());
        project.setTechnologies(new ArrayList<>());
        project.setContributors(new ArrayList<>());
        return project;
    }
}