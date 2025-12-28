package org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService;

import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ContributorDTO;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectImageDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.Contributor;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.service.SlugGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectMapper {

    private final SlugGenerator slugGenerator;

    @Transactional
    public Project toEntity(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }

        Project project = new Project();
        updateProjectFromDTO(project, dto);

        if (dto.getSlug() == null || dto.getSlug().isEmpty()) {
            project.setSlug(slugGenerator.generateUniqueSlug(dto.getName()));
        }

        return project;
    }

    public ProjectDTO toDTO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setSlug(project.getSlug());
        dto.setDescription(project.getDescription());
        dto.setRepositoryUrl(project.getRepositoryUrl());
        dto.setStatus(project.getStatus());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());

        dto.setFeatures(new ArrayList<>(project.getFeatures()));
        dto.setTechnologies(new ArrayList<>(project.getTechnologies()));

        if (project.getContributors() != null) {
            List<ContributorDTO> contributorDTOs = project.getContributors().stream()
                    .map(this::contributorToDTO)
                    .collect(Collectors.toList());
            dto.setContributors(contributorDTOs);
        }

        if (project.getProjectImages() != null) {
            List<ProjectImageDTO> imageDTOs = project.getProjectImages().stream()
                    .map(this::imageToDTO)
                    .collect(Collectors.toList());
            dto.setProjectImages(imageDTOs);
        }

        return dto;
    }

    public void updateProjectFromDTO(Project project, ProjectDTO dto) {
        if (project == null || dto == null) {
            return;
        }

        project.setName(dto.getName());
        project.setSlug(dto.getSlug());
        project.setDescription(dto.getDescription());
        project.setRepositoryUrl(dto.getRepositoryUrl());
        project.setStatus(dto.getStatus());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());

        if (dto.getFeatures() != null) {
            project.setFeatures(new ArrayList<>(dto.getFeatures()));
        }

        if (dto.getTechnologies() != null) {
            project.setTechnologies(new ArrayList<>(dto.getTechnologies()));
        }

        project.getContributors().clear();
        if (dto.getContributors() != null) {
            dto.getContributors().forEach(contributorDTO -> project.getContributors().add(contributorToEntity(contributorDTO)));
        }

        if (dto.getProjectImages() != null) {
            project.getProjectImages().clear();
            dto.getProjectImages().forEach(imageDTO -> {
                ImageFromProject image = new ImageFromProject();
                image.setId(imageDTO.getId());
                image.setImageUrl(imageDTO.getImageUrl());
                image.setCaption(imageDTO.getCaption());
                image.setProject(project);
                project.getProjectImages().add(image);
            });
        }
    }

    private ContributorDTO contributorToDTO(Contributor contributor) {
        if (contributor == null) {
            return null;
        }

        ContributorDTO dto = new ContributorDTO();
        dto.setName(contributor.getName());
        dto.setRole(contributor.getRole());
        dto.setProfileUrl(contributor.getProfileUrl());
        return dto;
    }

    private Contributor contributorToEntity(ContributorDTO dto) {
        if (dto == null) {
            return null;
        }

        Contributor contributor = new Contributor();
        contributor.setName(dto.getName());
        contributor.setRole(dto.getRole());
        contributor.setProfileUrl(dto.getProfileUrl());
        return contributor;
    }

    private ProjectImageDTO imageToDTO(ImageFromProject image) {
        if (image == null) return null;

        ProjectImageDTO dto = new ProjectImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageUrl(image.getImageUrl());
        dto.setCaption(image.getCaption());
        return dto;
    }
}
