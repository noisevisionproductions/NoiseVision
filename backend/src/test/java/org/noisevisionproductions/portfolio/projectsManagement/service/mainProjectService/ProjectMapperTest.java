package org.noisevisionproductions.portfolio.projectsManagement.service.mainProjectService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.projectsManagement.dto.ContributorDTO;
import org.noisevisionproductions.portfolio.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.portfolio.projectsManagement.model.Contributor;
import org.noisevisionproductions.portfolio.projectsManagement.model.Project;
import org.noisevisionproductions.portfolio.projectsManagement.model.ProjectStatus;
import org.noisevisionproductions.portfolio.projectsManagement.service.SlugGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {

    @Mock
    private SlugGenerator slugGenerator;

    @InjectMocks
    private ProjectMapper projectMapper;

    @Test
    void toEntity_ShouldReturnNull_WhenDTOIsNull() {
        assertThat(projectMapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_ShouldGenerateSlug_WhenSlugIsEmpty() {
        ProjectDTO dto = new ProjectDTO();
        dto.setName("Test Project");
        String expectedSlug = "test-project";

        when(slugGenerator.generateUniqueSlug(dto.getName())).thenReturn(expectedSlug);

        Project result = projectMapper.toEntity(dto);

        assertThat(result.getSlug()).isEqualTo(expectedSlug);
        verify(slugGenerator).generateUniqueSlug(dto.getName());
    }

    @Test
    void toEntity_ShouldNotGenerateSlug_WhenSlugExists() {
        ProjectDTO dto = new ProjectDTO();
        dto.setName("Test Project");
        dto.setSlug("existing-slug");

        Project result = projectMapper.toEntity(dto);

        assertThat(result.getSlug()).isEqualTo("existing-slug");
        verify(slugGenerator, never()).generateUniqueSlug(anyString());
    }

    @Test
    void toDTO_ShouldReturnNull_WhenProjectIsNull() {
        assertThat(projectMapper.toDTO(null)).isNull();
    }

    @Test
    void toDTO_ShouldMapAllFields_WhenProjectIsComplete() {
        Project project = createCompleteProject();

        ProjectDTO result = projectMapper.toDTO(project);

        assertThat(result)
                .satisfies(dto -> {
                    assertThat(dto.getName()).isEqualTo(project.getName());
                    assertThat(dto.getSlug()).isEqualTo(project.getSlug());
                    assertThat(dto.getDescription()).isEqualTo(project.getDescription());
                    assertThat(dto.getRepositoryUrl()).isEqualTo(project.getRepositoryUrl());
                    assertThat(dto.getStatus()).isEqualTo(project.getStatus());
                    assertThat(dto.getStartDate()).isEqualTo(project.getStartDate());
                    assertThat(dto.getEndDate()).isEqualTo(project.getEndDate());
                    assertThat(dto.getFeatures()).containsExactlyElementsOf(project.getFeatures());
                    assertThat(dto.getTechnologies()).containsExactlyElementsOf(project.getTechnologies());
                });
    }

    @Test
    void toDTO_ShouldMapContributors_WhenContributorsExist() {
        Project project = new Project();
        Contributor contributor = new Contributor();
        contributor.setName("John Doe");
        contributor.setRole("Developer");
        contributor.setProfileUrl("https://github.com/johndoe");
        project.setContributors(new ArrayList<>(List.of(contributor)));

        ProjectDTO result = projectMapper.toDTO(project);

        assertThat(result.getContributors())
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.getName()).isEqualTo("John Doe");
                    assertThat(dto.getRole()).isEqualTo("Developer");
                    assertThat(dto.getProfileUrl()).isEqualTo("https://github.com/johndoe");
                });
    }

    @Test
    void updateProjectFromDTO_ShouldNotUpdateAnything_WhenBothAreNull() {
        projectMapper.updateProjectFromDTO(null, null);
    }

    @Test
    void updateProjectFromDTO_ShouldUpdateAllFields_WhenDTOIsComplete() {
        Project project = new Project();
        ProjectDTO dto = createCompleteDTO();

        projectMapper.updateProjectFromDTO(project, dto);

        assertThat(project)
                .satisfies(p -> {
                    assertThat(p.getName()).isEqualTo(dto.getName());
                    assertThat(p.getDescription()).isEqualTo(dto.getDescription());
                    assertThat(p.getRepositoryUrl()).isEqualTo(dto.getRepositoryUrl());
                    assertThat(p.getStatus()).isEqualTo(dto.getStatus());
                    assertThat(p.getStartDate()).isEqualTo(dto.getStartDate());
                    assertThat(p.getEndDate()).isEqualTo(dto.getEndDate());
                    assertThat(p.getFeatures()).containsExactlyElementsOf(dto.getFeatures());
                    assertThat(p.getTechnologies()).containsExactlyElementsOf(dto.getTechnologies());
                });
    }

    @Test
    void updateProjectFromDTO_ShouldUpdateContributors_WhenDTOHasContributors() {
        Project project = new Project();
        project.setContributors(new ArrayList<>());

        ProjectDTO dto = new ProjectDTO();
        ContributorDTO contributorDTO = new ContributorDTO();
        contributorDTO.setName("Jane Smith");
        contributorDTO.setRole("Designer");
        contributorDTO.setProfileUrl("https://github.com/janesmith");
        dto.setContributors(List.of(contributorDTO));

        projectMapper.updateProjectFromDTO(project, dto);

        assertThat(project.getContributors())
                .hasSize(1)
                .first()
                .satisfies(contributor -> {
                    assertThat(contributor.getName()).isEqualTo("Jane Smith");
                    assertThat(contributor.getRole()).isEqualTo("Designer");
                    assertThat(contributor.getProfileUrl()).isEqualTo("https://github.com/janesmith");
                });
    }

    private Project createCompleteProject() {
        Project project = new Project();
        project.setName("Test Project");
        project.setSlug("test-project");
        project.setDescription("Test Description");
        project.setRepositoryUrl("https://github.com/test");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setStartDate(new Date());
        project.setEndDate(new Date());
        project.setFeatures(List.of("Feature 1", "Feature 2"));
        project.setTechnologies(List.of("Java", "Spring"));
        project.setContributors(new ArrayList<>());
        return project;
    }

    private ProjectDTO createCompleteDTO() {
        ProjectDTO dto = new ProjectDTO();
        dto.setName("Test Project");
        dto.setDescription("Test Description");
        dto.setRepositoryUrl("https://github.com/test");
        dto.setStatus(ProjectStatus.IN_PROGRESS);
        dto.setStartDate(new Date());
        dto.setEndDate(new Date());
        dto.setFeatures(List.of("Feature 1", "Feature 2"));
        dto.setTechnologies(List.of("Java", "Spring"));
        dto.setContributors(new ArrayList<>());
        return dto;
    }
}