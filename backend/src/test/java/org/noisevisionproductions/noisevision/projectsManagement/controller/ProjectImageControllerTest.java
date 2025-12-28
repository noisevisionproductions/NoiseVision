package org.noisevisionproductions.noisevision.projectsManagement.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectImageDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.service.FileStorageService;
import org.noisevisionproductions.noisevision.projectsManagement.service.ProjectImageService;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectImageControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ProjectImageService projectImageService;

    @InjectMocks
    private ProjectImageController projectImageController;

    @Test
    void addImageToProject_ShouldReturnCreatedImage_WhenValidRequest() {
        Long projectId = 1L;
        Long imageId = 1L;
        ProjectImageDTO imageDTO = new ProjectImageDTO();
        imageDTO.setImageUrl("http://example.com/image.jpg");
        imageDTO.setCaption("Test image");

        ImageFromProject addedImage = new ImageFromProject();
        addedImage.setId(imageId);
        addedImage.setImageUrl(imageDTO.getImageUrl());
        addedImage.setCaption(imageDTO.getCaption());

        when(projectImageService.addImageToProject(eq(projectId), any(ProjectImageDTO.class)))
                .thenReturn(addedImage);

        ResponseEntity<ProjectImageDTO> response = projectImageController.addImageToProject(projectId, imageDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(imageId);
        assertThat(response.getBody().getImageUrl()).isEqualTo(imageDTO.getImageUrl());
        assertThat(response.getBody().getCaption()).isEqualTo(imageDTO.getCaption());
        assertThat(response.getHeaders().getLocation())
                .isEqualTo(URI.create("/api/projects/" + projectId + "/images/" + imageId));

        verify(projectImageService).addImageToProject(projectId, imageDTO);
    }

    @Test
    void uploadProjectImage_ShouldReturnCreatedImage_WhenValidFile() {
        Long projectId = 1L;
        Long imageId = 1L;
        String fileName = "test-image.jpg";
        String storedFileUrl = "http://storage.com/" + fileName;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "image/jpeg",
                "test image content".getBytes()
        );

        Project project = new Project();
        project.setId(projectId);

        ImageFromProject addedImage = new ImageFromProject();
        addedImage.setId(imageId);
        addedImage.setImageUrl(storedFileUrl);
        addedImage.setCaption(fileName);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn(storedFileUrl);
        when(projectImageService.addImageToProject(eq(projectId), any(ProjectImageDTO.class)))
                .thenReturn(addedImage);

        ResponseEntity<ProjectImageDTO> response = projectImageController.uploadProjectImage(projectId, file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(imageId);
        assertThat(response.getBody().getImageUrl()).isEqualTo(storedFileUrl);
        assertThat(response.getBody().getCaption()).isEqualTo(fileName);
        assertThat(response.getHeaders().getLocation())
                .isEqualTo(URI.create("/api/projects/" + projectId + "/images/" + imageId));

        verify(projectService).getProjectById(projectId);
        verify(fileStorageService).storeFile(file);
        verify(projectImageService).addImageToProject(eq(projectId), any(ProjectImageDTO.class));
    }

    @Test
    void uploadProjectImage_ShouldReturnBadRequest_WhenEmptyFile() {
        Long projectId = 1L;
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "",
                "image/jpeg",
                new byte[0]
        );

        ResponseEntity<ProjectImageDTO> response = projectImageController.uploadProjectImage(projectId, emptyFile);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();

        verifyNoInteractions(fileStorageService);
        verify(projectImageService, never()).addImageToProject(any(), any());
    }

    @Test
    void uploadProjectImage_ShouldReturnNotFound_WhenProjectNotFound() {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        when(projectService.getProjectById(projectId)).thenReturn(null);
        ResponseEntity<ProjectImageDTO> response = projectImageController.uploadProjectImage(projectId, file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(projectService).getProjectById(projectId);
        verifyNoInteractions(fileStorageService);
        verify(projectImageService, never()).addImageToProject(any(), any());
    }

    @Test
    void uploadProjectImage_ShouldThrowException_WhenStorageFails() {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        Project project = new Project();
        project.setId(projectId);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(fileStorageService.storeFile(any(MultipartFile.class)))
                .thenThrow(new RuntimeException("Storage error"));

        assertThatThrownBy(() -> projectImageController.uploadProjectImage(projectId, file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not store file");

        verify(projectService).getProjectById(projectId);
        verify(fileStorageService).storeFile(file);
        verify(projectImageService, never()).addImageToProject(any(), any());
    }

    @Test
    void removeImageFromProject_ShouldReturnNoContent_WhenImageRemoved() {
        Long projectId = 1L;
        Long imageId = 1L;

        ResponseEntity<Void> response = projectImageController.removeImageFromProject(projectId, imageId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(projectImageService).removeImageFromProject(projectId, imageId);
    }
}