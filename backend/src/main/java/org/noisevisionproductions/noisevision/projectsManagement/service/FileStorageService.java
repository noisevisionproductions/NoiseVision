package org.noisevisionproductions.noisevision.projectsManagement.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.noisevisionproductions.noisevision.projectsManagement.component.FileStorageProperties;
import org.noisevisionproductions.noisevision.projectsManagement.exceptions.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileStorageProperties properties;
    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(properties.getUploadDir())
                .toAbsolutePath()
                .normalize();

        log.info("Files will be stored in: {}", this.fileStorageLocation);

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Storage directory created/verified successfully");
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        log.info("Attempting to store file: {}", fileName);

        if (fileName.contains("..")) {
            throw new FileStorageException("Invalid filename: " + fileName);
        }

        try {
            String uniqueFileName = UUID.randomUUID() + "_" + fileName;
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);

            log.info("Storing file to: {}", targetLocation.toAbsolutePath());

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            log.info("File stored successfully: {}", uniqueFileName);
            return "/api/files/" + uniqueFileName;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + fileName, e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new FileNotFoundException("File not found: " + fileName);
            }
            return resource;
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new FileStorageException("File not found: " + fileName, e);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file: " + fileName, e);
        }
    }
}
