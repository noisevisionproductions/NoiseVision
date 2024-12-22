package org.noisevisionproductions.portfolio.projectsManagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.portfolio.projectsManagement.dto.ContributorDTO;
import org.noisevisionproductions.portfolio.projectsManagement.model.Contributor;
import org.noisevisionproductions.portfolio.projectsManagement.model.Project;
import org.noisevisionproductions.portfolio.projectsManagement.repository.ProjectRepository;
import org.noisevisionproductions.portfolio.projectsManagement.service.mainProjectService.ProjectService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectContributorServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectCacheService projectCacheService;

    @InjectMocks
    private ProjectContributorService projectContributorService;

    @Test
    void addContributor_ShouldAddContributorToProject_WhenValidDataProvided() {
        Long projectId = 1L;
        ContributorDTO contributorDTO = new ContributorDTO();

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setContributors(new ArrayList<>());

        Project savedProject = new Project();
        savedProject.setId(projectId);

        Contributor savedContributor = new Contributor();
        savedContributor.setName(contributorDTO.getName());
        savedContributor.setRole(contributorDTO.getRole());
        savedContributor.setProfileUrl(contributorDTO.getProfileUrl());
        savedProject.setContributors(new ArrayList<>(List.of(savedContributor)));

        when(projectService.getProjectById(projectId)).thenReturn(existingProject);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        Project result = projectContributorService.addContributor(projectId, contributorDTO);

        verify(projectService).getProjectById(projectId);
        verify(projectRepository).save(any(Project.class));
        verify(projectCacheService).cache(projectId, savedProject);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(projectId);
        assertThat(result.getContributors()).hasSize(1);

        Contributor addedContributor = result.getContributors().getFirst();
        assertThat(addedContributor.getName()).isEqualTo(contributorDTO.getName());
        assertThat(addedContributor.getRole()).isEqualTo(contributorDTO.getRole());
        assertThat(addedContributor.getProfileUrl()).isEqualTo(contributorDTO.getProfileUrl());
    }

    @Test
    void addContributor_ShouldThrowException_WhenProjectNotFound() {
        Long nonExistingProjectId = 999L;
        ContributorDTO contributorDTO = new ContributorDTO();

        when(projectService.getProjectById(nonExistingProjectId))
                .thenThrow(new RuntimeException("Project not found"));

        assertThatThrownBy(() -> projectContributorService.addContributor(nonExistingProjectId, contributorDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Project not found");

        verify(projectService).getProjectById(nonExistingProjectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void addContributor_ShouldAddContributor_WhenProjectAlreadyHasContributors() {
        Long projectId = 1L;
        ContributorDTO newContributorDto = new ContributorDTO();
        newContributorDto.setName("Jane Doe");
        newContributorDto.setRole("Designer");
        newContributorDto.setProfileUrl("https://github.com/janedoe");

        Project existingProject = new Project();
        existingProject.setId(projectId);
        Contributor existingContributor = new Contributor();
        existingContributor.setName("John Doe");
        existingContributor.setRole("Developer");
        existingProject.setContributors(new ArrayList<>(Set.of(existingContributor)));

        Project savedProject = new Project();
        savedProject.setId(projectId);
        List<Contributor> savedContributors = new ArrayList<>();
        savedContributors.add(existingContributor);

        Contributor newContributor = new Contributor();
        newContributor.setName(newContributorDto.getName());
        newContributor.setRole(newContributorDto.getRole());
        newContributor.setProfileUrl(newContributorDto.getProfileUrl());
        savedContributors.add(newContributor);
        savedProject.setContributors(savedContributors);

        when(projectService.getProjectById(projectId)).thenReturn(existingProject);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        Project result = projectContributorService.addContributor(projectId, newContributorDto);

        verify(projectService).getProjectById(projectId);
        verify(projectRepository).save(any(Project.class));
        verify(projectCacheService).cache(projectId, savedProject);

        assertThat(result).isNotNull();
        assertThat(result.getContributors()).hasSize(2);
        assertThat(result.getContributors())
                .extracting(Contributor::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Doe");
    }
}