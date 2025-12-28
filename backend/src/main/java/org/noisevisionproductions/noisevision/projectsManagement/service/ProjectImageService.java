package org.noisevisionproductions.noisevision.projectsManagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.noisevisionproductions.noisevision.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectImageDTO;
import org.noisevisionproductions.noisevision.projectsManagement.exceptions.FileStorageException;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectImageService {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final FileStorageService fileStorageService;
    private final ProjectCacheService projectCacheService;

    public ImageFromProject addImageToProject(Long projectId, ProjectImageDTO projectImageDTO) {
        Project project = projectService.getProjectById(projectId);

        ImageFromProject image = new ImageFromProject();
        image.setImageUrl(projectImageDTO.getImageUrl());
        image.setCaption(projectImageDTO.getCaption());
        image.setProject(project);

        project.getProjectImages().add(image);

        Project savedProject = projectRepository.saveAndFlush(project);

        ImageFromProject savedImage = savedProject.getProjectImages()
                .stream()
                .filter(img -> img.getImageUrl().equals(projectImageDTO.getImageUrl()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to save image"));

        projectCacheService.cache(projectId, savedProject);

        return savedImage;
    }

    public void removeImageFromProject(Long projectId, Long imageId) {
        Project project = projectService.getProjectById(projectId);

        Optional<ImageFromProject> imageToRemove = project.getProjectImages()
                .stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst();

        if (imageToRemove.isPresent()) {
            ImageFromProject image = imageToRemove.get();
            try {
                fileStorageService.deleteFile(image.getImageUrl());
            } catch (FileStorageException e) {
                log.error("Failed to delete file for image: {}", image.getImageUrl(), e);
            }

            project.getProjectImages().removeIf(img -> img.getId().equals(imageId));
            Project udpatedProject = projectRepository.save(project);

            projectCacheService.cache(projectId, udpatedProject);
        }
    }
}
