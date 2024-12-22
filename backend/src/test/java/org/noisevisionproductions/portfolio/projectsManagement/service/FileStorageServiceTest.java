package org.noisevisionproductions.portfolio.projectsManagement.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.noisevisionproductions.portfolio.projectsManagement.component.FileStorageProperties;
import org.noisevisionproductions.portfolio.projectsManagement.exceptions.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;
    private FileStorageProperties properties;

    @BeforeEach
    void setUp() {
        properties = mock(FileStorageProperties.class);
        when(properties.getUploadDir()).thenReturn(tempDir.toString());

        fileStorageService = new FileStorageService(properties);
        fileStorageService.init();
    }

    @AfterEach
    void cleanup() throws IOException {
        try (var walk = Files.walk(tempDir)) {
            walk.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            // Ignorujemy błędy przy czyszczeniu
                        }
                    });
        }
    }


    @Test
    void init_ShouldCreateDirectory_WhenDirectoryDoesNotExist() throws IOException {
        Path newTempDir = tempDir.resolve("newDir");
        when(properties.getUploadDir()).thenReturn(newTempDir.toString());

        FileStorageService newService = new FileStorageService(properties);

        newService.init();

        assertThat(Files.exists(newTempDir)).isTrue();
        assertThat(Files.isDirectory(newTempDir)).isTrue();
    }

    @Test
    void storeFile_ShouldSaveFile_WhenValidFile() throws IOException {
        String fileName = "test.txt";
        String content = "test content";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "text/plain",
                content.getBytes()
        );

        String savedFilePath = fileStorageService.storeFile(file);

        assertThat(savedFilePath).startsWith("/api/files/");
        assertThat(savedFilePath).endsWith(fileName);

        String actualFileName = savedFilePath.substring(savedFilePath.lastIndexOf('/') + 1);
        Path savedFile = tempDir.resolve(actualFileName);
        assertThat(Files.exists(savedFile)).isTrue();
        assertThat(Files.readString(savedFile)).isEqualTo(content);
    }

    @Test
    void storeFile_ShouldThrowException_WhenFileNameContainDots() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../test.txt",
                "text/plain",
                "content".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(file))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Invalid filename");
    }

    @Test
    void loadFileAsResource_ShouldLoadFile_WhenFileExists() throws IOException {
        String content = "test content";
        String fileName = UUID.randomUUID() + "_test.txt";
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, content.getBytes());

        Resource resource = fileStorageService.loadFileAsResource(fileName);

        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();

        try (var inputStream = resource.getInputStream()) {
            String fileContent = new String(inputStream.readAllBytes());
            assertThat(fileContent).isEqualTo(content);
        }
    }

    @Test
    void loadFileAsResource_ShouldThrowException_WhenFileNotFound() {
        String nonExistentFileName = "nonexistent.txt";

        assertThatThrownBy(() -> fileStorageService.loadFileAsResource(nonExistentFileName))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    void deleteFile_ShouldDeleteFile_WhenFileExists() throws IOException {
        String fileName = UUID.randomUUID() + "_text.txt";
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, "content".getBytes());
        String fileUrl = "/api/files/" + fileName;

        fileStorageService.deleteFile(fileUrl);

        assertThat(Files.exists(filePath)).isFalse();
    }

    @Test
    void deleteFile_ShouldNotThrowException_WhenFileDoesNotExists() {
        String nonExistentFileUrl = "/api/files/nonexistent.txt";

        assertThatCode(() -> fileStorageService.deleteFile(nonExistentFileUrl))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteFile_ShouldDoNothing_WhenUrlIsNull() {
        assertThatCode(() -> fileStorageService.deleteFile(null))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteFile_ShouldDoNothing_WhenUrlIsEmpty() {
        assertThatCode(() -> fileStorageService.deleteFile(""))
                .doesNotThrowAnyException();
    }
}