package org.noisevisionproductions.noisevision.projectsManagement.service;

import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ContributorDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.Contributor;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectContributorService {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final ProjectCacheService projectCacheService;

    public Project addContributor(Long projectId, ContributorDTO contributorDTO) {
        Project project = projectService.getProjectById(projectId);
        Contributor contributor = new Contributor();
        contributor.setName(contributorDTO.getName());
        contributor.setRole(contributorDTO.getRole());
        contributor.setProfileUrl(contributorDTO.getProfileUrl());
        project.getContributors().add(contributor);

        Project updatedProject = projectRepository.save(project);
        projectCacheService.cache(projectId, updatedProject);

        return updatedProject;
    }
}
