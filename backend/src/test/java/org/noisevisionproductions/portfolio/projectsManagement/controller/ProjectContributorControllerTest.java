package org.noisevisionproductions.portfolio.projectsManagement.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.projectsManagement.dto.ContributorDTO;
import org.noisevisionproductions.portfolio.projectsManagement.model.Project;
import org.noisevisionproductions.portfolio.projectsManagement.service.ProjectContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectContributorControllerTest {

    @Mock
    private ProjectContributorService projectContributorService;

    @InjectMocks
    private ProjectContributorController projectContributorController;

    @Test
    void addContributor_ShouldReturnUpdatedProject_WhenValidRequest() {
        Long projectId = 1L;
        ContributorDTO contributorDTO = new ContributorDTO();

        Project expectedProject = new Project();
        expectedProject.setId(projectId);

        when(projectContributorService.addContributor(eq(projectId), any(ContributorDTO.class)))
                .thenReturn(expectedProject);

        ResponseEntity<Project> response = projectContributorController.addContributor(
                projectId,
                contributorDTO
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(projectId);

        verify(projectContributorService, times(1)).addContributor(eq(projectId), eq(contributorDTO));
    }

    @Test
    void addContributor_ShouldCallProjectService_WithCorrectParameters() {
        Long projectId = 1L;
        ContributorDTO contributorDTO = new ContributorDTO();

        Project mockProject = new Project();
        when(projectContributorService.addContributor(any(), any())).thenReturn(mockProject);

        projectContributorController.addContributor(projectId, contributorDTO);

        verify(projectContributorService).addContributor(projectId, contributorDTO);
    }
}