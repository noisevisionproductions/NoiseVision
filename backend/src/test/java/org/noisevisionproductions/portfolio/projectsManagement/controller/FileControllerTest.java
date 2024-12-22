package org.noisevisionproductions.portfolio.projectsManagement.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.projectsManagement.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private Resource resource;

    @Mock
    private ServletContext servletContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private File file;

    @InjectMocks
    private FileController fileController;

    @Test
    void getFile_WhenFileExists_ReturnOkResponse() throws IOException {
        // Given
        String fileName = "test-image.jpg";
        String filePath = "/path/to/" + fileName;
        String contentType = "image/jpeg";

        when(request.getServletContext()).thenReturn(servletContext);
        when(fileStorageService.loadFileAsResource(fileName)).thenReturn(resource);
        when(resource.getFile()).thenReturn(file);
        when(file.getAbsolutePath()).thenReturn(filePath);
        when(servletContext.getMimeType(filePath)).thenReturn(contentType);
        when(resource.getFilename()).thenReturn(fileName);

        ResponseEntity<Resource> response = fileController.getFile(fileName, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(resource);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType(contentType));
        assertThat(response.getHeaders().getContentDisposition().toString())
                .contains("inline")
                .contains(fileName);

        verify(fileStorageService).loadFileAsResource(fileName);
        verify(servletContext).getMimeType(filePath);
    }

    @Test
    @SneakyThrows
    void getFile_ShouldUseDefaultContentType_WhenMimeTypeIsNull() {
        String fileName = "test-file.xyz";
        String filePath = "/path/to" + fileName;

        when(request.getServletContext()).thenReturn(servletContext);
        when(fileStorageService.loadFileAsResource(fileName)).thenReturn(resource);
        when(resource.getFile()).thenReturn(file);
        when(file.getAbsolutePath()).thenReturn(filePath);
        when(servletContext.getMimeType(filePath)).thenReturn(null);
        when(resource.getFilename()).thenReturn(fileName);

        ResponseEntity<Resource> response = fileController.getFile(fileName, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(resource);
        assertThat(response.getHeaders().getContentType())
                .isEqualTo(MediaType.parseMediaType("application/octet-stream"));

        verify(fileStorageService).loadFileAsResource(fileName);
        verify(servletContext).getMimeType(filePath);
    }

    @Test
    @SneakyThrows
    void getFile_ShouldReturnInternalServerError_WhenExceptionOccurs() {
        String fileName = "non-existing-file.txt";

        when(fileStorageService.loadFileAsResource(fileName))
                .thenThrow(new RuntimeException("File not found"));

        ResponseEntity<Resource> response = fileController.getFile(fileName, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();

        verify(fileStorageService).loadFileAsResource(fileName);
        verifyNoInteractions(servletContext);
    }
}