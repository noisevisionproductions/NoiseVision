package org.noisevisionproductions.portfolio.projectsManagement.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.projectsManagement.model.Project;
import org.noisevisionproductions.portfolio.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectFeatureServiceTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectFeatureController projectFeatureController;

    @Test
    void updateFeatures_ShouldReturnUpdatedProject_WhenValidRequest() {
        Long projectId = 1L;
        List<String> features = List.of(
                "Docker",
                "CI/CD",
                "Unit Testing"
        );

        Project expectedProject = new Project();
        expectedProject.setId(projectId);
        expectedProject.setFeatures(features);

        when(projectService.updateFeatures(eq(projectId), anyList()))
                .thenReturn(expectedProject);

        ResponseEntity<Project> response = projectFeatureController.updateFeatures(
                projectId,
                features
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(projectId);
        assertThat(response.getBody().getFeatures())
                .containsExactlyElementsOf(features);

        verify(projectService, times(1)).updateFeatures(eq(projectId), eq(features));
    }

    @Test
    void updateFeatures_ShouldCallProjectService_WithCorrectParameters() {
        Long projectId = 1L;
        List<String> features = List.of(
                "Docker",
                "CI/CD",
                "Unit Testing"
        );

        Project mockProject = new Project();
        when(projectService.updateFeatures(any(), any())).thenReturn(mockProject);

        projectFeatureController.updateFeatures(projectId, features);

        verify(projectService).updateFeatures(projectId, features);
    }

    @Test
    void updateFeatures_ShouldHandleEmptyFeaturesList() {
        Long projectId = 1L;
        List<String> features = List.of();

        Project exptectedProject = new Project();
        exptectedProject.setId(projectId);
        exptectedProject.setFeatures(features);

        when(projectService.updateFeatures(eq(projectId), anyList()))
                .thenReturn(exptectedProject);

        ResponseEntity<Project> response = projectFeatureController.updateFeatures(
                projectId,
                features
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFeatures()).isEmpty();

        verify(projectService).updateFeatures(projectId, features);
    }
}