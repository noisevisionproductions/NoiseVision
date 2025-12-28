package org.noisevisionproductions.noisevision.projectsManagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectImageDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.service.FileStorageService;
import org.noisevisionproductions.noisevision.projectsManagement.service.ProjectImageService;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectImageController {

    private final ProjectService projectService;
    private final FileStorageService fileStorageService;
    private final ProjectImageService projectImageService;

    @PostMapping("/{projectId}/images")
    @PreAuthorize("hasAuthority('EDIT_PROJECTS')")
    public ResponseEntity<ProjectImageDTO> addImageToProject(
            @PathVariable Long projectId,
            @RequestBody ProjectImageDTO imageDTO
    ) {
        ImageFromProject added = projectImageService.addImageToProject(projectId, imageDTO);

        ProjectImageDTO responseDTO = new ProjectImageDTO();
        responseDTO.setId(added.getId());
        responseDTO.setImageUrl(added.getImageUrl());
        responseDTO.setCaption(added.getCaption());

        return ResponseEntity.created(URI.create("/api/projects/" + projectId + "/images/" + added.getId()))
                .body(responseDTO);
    }

    @PostMapping("/{projectId}/images/upload")
    @PreAuthorize("hasAuthority('EDIT_PROJECTS')")
    public ResponseEntity<ProjectImageDTO> uploadProjectImage(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                log.warn("Empty file received");
                return ResponseEntity.badRequest().build();
            }

            Project project = projectService.getProjectById(projectId);
            if (project == null) {
                return ResponseEntity.notFound().build();
            }
            log.info("Processing file: {}", file.getOriginalFilename());
            String imageUrl = fileStorageService.storeFile(file);
            log.info("File stored, URL: {}", imageUrl);

            ProjectImageDTO imageDTO = new ProjectImageDTO();
            imageDTO.setImageUrl(imageUrl);
            imageDTO.setCaption(file.getOriginalFilename());

            ImageFromProject added = projectImageService.addImageToProject(projectId, imageDTO);
            log.info("Image added to project, ID: {}", added.getId());

            ProjectImageDTO responseDTO = new ProjectImageDTO();
            responseDTO.setId(added.getId());
            responseDTO.setImageUrl(added.getImageUrl());
            responseDTO.setCaption(added.getCaption());

            return ResponseEntity.created(URI.create("/api/projects/" + projectId + "/images/" + added.getId()))
                    .body(responseDTO);
        } catch (Exception e) {
            throw new RuntimeException("Could not store file. Please try again!", e);
        }
    }

    @DeleteMapping("/{projectId}/images/{imageId}")
    @PreAuthorize("hasAuthority('EDIT_PROJECTS')")
    public ResponseEntity<Void> removeImageFromProject(
            @PathVariable Long projectId,
            @PathVariable Long imageId
    ) {
        projectImageService.removeImageFromProject(projectId, imageId);
        return ResponseEntity.noContent().build();
    }
}
