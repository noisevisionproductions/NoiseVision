package org.noisevisionproductions.noisevision.projectsManagement.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectMapper;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void createProject_ShouldReturnCreatedProject_WhenValidRequest() {
        ProjectDTO projectDTO = new ProjectDTO();
        Project createdProject = new Project();
        createdProject.setId(1L);
        ProjectDTO mappedDTO = new ProjectDTO();
        mappedDTO.setId(1L);

        when(projectService.createProject(any(ProjectDTO.class))).thenReturn(createdProject);
        when(projectMapper.toDTO(createdProject)).thenReturn(mappedDTO);

        ResponseEntity<ProjectDTO> response = projectController.createProject(projectDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getHeaders().getLocation())
                .isEqualTo(URI.create("/api/projects/1"));

        verify(projectService).createProject(projectDTO);
        verify(projectMapper).toDTO(createdProject);
    }

    @Test
    void getAllProjects_ShouldReturnListOfProjects() {
        List<Project> projects = List.of(
                createTestProject(1L, "Project 1", "project-1"),
                createTestProject(2L, "Project 2", "project-2")
        );
        List<ProjectDTO> projectDTOs = List.of(
                createTestProjectDTO(1L, "Project 1", "project-1"),
                createTestProjectDTO(2L, "Project 2", "project-2")
        );

        when(projectService.getAllProjects()).thenReturn(projects);
        when(projectMapper.toDTO(projects.get(0))).thenReturn(projectDTOs.get(0));
        when(projectMapper.toDTO(projects.get(1))).thenReturn(projectDTOs.get(1));

        ResponseEntity<List<ProjectDTO>> response = projectController.getAllProjects();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .hasSize(2)
                .containsExactlyElementsOf(projectDTOs);

        verify(projectService).getAllProjects();
        verify(projectMapper, times(2)).toDTO(any(Project.class));
    }


    @Test
    void getProjectBySlug_ShouldReturnProject_WhenProjectExists() {
        String slug = "test-project";
        Project project = createTestProject(1L, "Test Project", slug);
        ProjectDTO projectDTO = createTestProjectDTO(1L, "Test Project", slug);

        when(projectService.getProjectBySlug(slug)).thenReturn(project);
        when(projectMapper.toDTO(project)).thenReturn(projectDTO);

        ResponseEntity<ProjectDTO> response = projectController.getProjectBySlug(slug);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(ProjectDTO::getSlug)
                .isEqualTo(slug);

        verify(projectService).getProjectBySlug(slug);
        verify(projectMapper).toDTO(project);
    }

    @Test
    void getProjectBySlug_ShouldReturnNotFound_WhenProjectDoesNotExists() {
        String slug = "nonexistent-project";
        when(projectService.getProjectBySlug(slug)).thenThrow(new RuntimeException("Project not found"));

        ResponseEntity<ProjectDTO> response = projectController.getProjectBySlug(slug);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(projectService).getProjectBySlug(slug);
    }

    @Test
    void getProjectById_ShouldReturnProject_WhenProjectExists() {
        Long projectId = 1L;
        Project project = createTestProject(projectId, "Test Project", "test-project");
        ProjectDTO projectDTO = createTestProjectDTO(projectId, "Test Project", "test-project");

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectMapper.toDTO(project)).thenReturn(projectDTO);

        ResponseEntity<ProjectDTO> response = projectController.getProjectById(projectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(ProjectDTO::getId)
                .isEqualTo(projectId);

        verify(projectService).getProjectById(projectId);
        verify(projectMapper).toDTO(project);
    }

    @Test
    void updateProject_ShouldReturnUpdatedProject_WhenValidRequest() {
        Long projectId = 1L;
        ProjectDTO requestDTO = new ProjectDTO();
        Project updatedProject = new Project();
        ProjectDTO responseDTO = new ProjectDTO();

        when(projectService.updateProject(eq(projectId), any(ProjectDTO.class)))
                .thenReturn(updatedProject);
        when(projectMapper.toDTO(updatedProject)).thenReturn(responseDTO);

        ResponseEntity<ProjectDTO> response = projectController.updateProject(projectId, requestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);

        verify(projectService).updateProject(projectId, requestDTO);
        verify(projectMapper).toDTO(updatedProject);
    }

    @Test
    void deleteProject_ShouldReturnNoContent_WhenProjectDeleted() {
        Long projectId = 1L;

        ResponseEntity<Void> response = projectController.deleteProject(projectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(projectService).deleteProject(projectId);
    }

    private Project createTestProject(long id, String name, String slug) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setSlug(slug);
        return project;
    }

    private ProjectDTO createTestProjectDTO(long id, String name, String slug) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setSlug(slug);
        return dto;
    }
}