package org.noisevisionproductions.portfolio.projectsManagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.portfolio.projectsManagement.dto.ProjectImageDTO;
import org.noisevisionproductions.portfolio.projectsManagement.exceptions.FileStorageException;
import org.noisevisionproductions.portfolio.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.portfolio.projectsManagement.model.Project;
import org.noisevisionproductions.portfolio.projectsManagement.repository.ProjectRepository;
import org.noisevisionproductions.portfolio.projectsManagement.service.mainProjectService.ProjectService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectImageServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ProjectCacheService projectCacheService;

    @InjectMocks
    private ProjectImageService projectImageService;

    @Test
    void addImageToProject_ShouldAddImageToProject_WhenValidDataProvided() {
        Long projectId = 1L;
        ProjectImageDTO projectImageDTO = new ProjectImageDTO();
        projectImageDTO.setImageUrl("/images/test.jpg");
        projectImageDTO.setCaption("Test Caption");

        Project project = new Project();
        project.setId(projectId);
        project.setProjectImages(new ArrayList<>());

        Project savedProject = new Project();
        savedProject.setId(projectId);
        ImageFromProject savedImage = new ImageFromProject();
        savedImage.setImageUrl(projectImageDTO.getImageUrl());
        savedImage.setCaption(projectImageDTO.getCaption());
        savedImage.setProject(savedProject);
        savedProject.setProjectImages(new ArrayList<>(List.of(savedImage)));

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.saveAndFlush(any(Project.class))).thenReturn(savedProject);

        ImageFromProject result = projectImageService.addImageToProject(projectId, projectImageDTO);

        verify(projectService).getProjectById(projectId);
        verify(projectRepository).saveAndFlush(project);
        verify(projectCacheService).cache(projectId, savedProject);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(projectImageDTO.getImageUrl());
        assertThat(result.getCaption()).isEqualTo(projectImageDTO.getCaption());
        assertThat(result.getProject()).isEqualTo(savedProject);
    }

    @Test
    void addImageToProject_ShouldNotCacheProject_WhenSaveFails() {
        Long projectId = 1L;
        ProjectImageDTO projectImageDTO = new ProjectImageDTO();
        projectImageDTO.setImageUrl("/images/test.jpg");

        Project project = new Project();
        project.setId(projectId);
        project.setProjectImages(new ArrayList<>());

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.saveAndFlush(any(Project.class))).thenThrow(new RuntimeException("Save failed"));

        assertThatThrownBy(() ->
                projectImageService.addImageToProject(projectId, projectImageDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Save failed");

        verify(projectService).getProjectById(projectId);
        verify(projectRepository).saveAndFlush(any(Project.class));
        verify(projectCacheService, never()).cache(eq(projectId), any(Project.class));
    }

    @Test
    void addImageToProject_ShouldUpdateCache_AfterSuccessfulSave() {
        Long projectId = 1L;
        ProjectImageDTO projectImageDTO = new ProjectImageDTO();
        projectImageDTO.setImageUrl("/images/test.jpg");
        projectImageDTO.setCaption("Test Caption");

        Project project = new Project();
        project.setId(projectId);
        project.setProjectImages(new ArrayList<>());

        Project savedProject = new Project();
        savedProject.setId(projectId);
        ImageFromProject savedImage = new ImageFromProject();
        savedImage.setImageUrl(projectImageDTO.getImageUrl());
        savedImage.setCaption(projectImageDTO.getCaption());
        savedImage.setProject(savedProject);
        savedProject.setProjectImages(new ArrayList<>(List.of(savedImage)));

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.saveAndFlush(any(Project.class))).thenReturn(savedProject);

        ImageFromProject result = projectImageService.addImageToProject(projectId, projectImageDTO);

        verify(projectService).getProjectById(projectId);
        verify(projectRepository).saveAndFlush(project);
        verify(projectCacheService).cache(projectId, savedProject);

        assertThat(result).isNotNull()
                .satisfies(image -> {
                    assertThat(image.getImageUrl()).isEqualTo(projectImageDTO.getImageUrl());
                    assertThat(image.getCaption()).isEqualTo(projectImageDTO.getCaption());
                    assertThat(image.getProject()).isEqualTo(savedProject);
                });
    }

    @Test
    void removeImageFromProject_ShouldRemoveImage_WhenImageExists() {
        Long projectId = 1L;
        Long imageId = 1L;
        String imageUrl = "/images/test.jpg";

        Project project = new Project();
        project.setId(projectId);

        ImageFromProject image = new ImageFromProject();
        image.setId(imageId);
        image.setImageUrl(imageUrl);
        image.setProject(project);

        project.setProjectImages(new ArrayList<>(List.of(image)));

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectImageService.removeImageFromProject(projectId, imageId);

        verify(projectService).getProjectById(projectId);
        verify(fileStorageService).deleteFile(imageUrl);
        verify(projectRepository).save(project);
        verify(projectCacheService).cache(projectId, project);
        assertThat(project.getProjectImages()).isEmpty();
    }

    @Test
    void removeImageFromProject_ShouldHandleFileStorageException_WhenDeletingFile() {
        Long projectId = 1L;
        Long imageId = 1L;
        String imageUrl = "/images/test.jpg";

        Project project = new Project();
        project.setId(projectId);

        ImageFromProject image = new ImageFromProject();
        image.setId(imageId);
        image.setImageUrl(imageUrl);
        project.setProjectImages(new ArrayList<>(List.of(image)));

        when(projectService.getProjectById(projectId)).thenReturn(project);
        doThrow(new FileStorageException("Error deleting file"))
                .when(fileStorageService).deleteFile(imageUrl);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectImageService.removeImageFromProject(projectId, imageId);

        verify(projectService).getProjectById(projectId);
        verify(fileStorageService).deleteFile(imageUrl);
        verify(projectRepository).save(project);
        verify(projectCacheService).cache(projectId, project);
        assertThat(project.getProjectImages()).isEmpty();
    }
}