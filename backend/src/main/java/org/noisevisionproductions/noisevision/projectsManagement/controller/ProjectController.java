package org.noisevisionproductions.noisevision.projectsManagement.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectMapper;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@Transactional
@Tag(name = "Projects", description = "API do zarządzania projektami i ich obrazami")
public class ProjectController {
    private final ProjectMapper projectMapper;
    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PROJECTS')")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        Project created = projectService.createProject(projectDTO);
        if (created.getId() == null) {
            throw new RuntimeException("Project ID was not generated");
        }
        return ResponseEntity.created(URI.create("/api/projects/" + created.getId()))
                .body(projectMapper.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects().stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProjectDTO> getProjectBySlug(@PathVariable String slug) {
        try {
            Project project = projectService.getProjectBySlug(slug);
            return ResponseEntity.ok(projectMapper.toDTO(project));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(projectMapper.toDTO(project));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_PROJECTS')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        Project updated = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(projectMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PROJECTS')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
