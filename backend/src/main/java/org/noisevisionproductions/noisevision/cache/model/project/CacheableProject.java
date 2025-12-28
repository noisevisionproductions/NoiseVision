package org.noisevisionproductions.noisevision.cache.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.model.ProjectStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CacheableProject implements Serializable {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String repositoryUrl;
    private ProjectStatus status;
    private Date createdAt;
    private Date startDate;
    private Date endDate;
    private Date lastModifiedAt;
    private List<String> features = new ArrayList<>();
    private List<String> technologies = new ArrayList<>();
    private List<CacheableContributor> contributors = new ArrayList<>();
    private List<CacheableImage> images = new ArrayList<>();

    public Project toEntity() {
        Project project = new Project();
        project.setId(this.getId());
        project.setName(this.getName());
        project.setSlug(this.getSlug());
        project.setDescription(this.getDescription());
        project.setRepositoryUrl(this.getRepositoryUrl());
        project.setStatus(this.getStatus());
        project.setStartDate(this.getStartDate());
        project.setEndDate(this.getEndDate());

        project.setFeatures(new ArrayList<>(this.getFeatures()));
        project.setTechnologies(new ArrayList<>(this.getTechnologies()));

        if (this.getContributors() != null) {
            project.setContributors(
                    this.getContributors().stream()
                            .map(CacheableContributor::toEntity)
                            .collect(Collectors.toList())
            );
        }

        if (this.getImages() != null) {
            project.setProjectImages(
                    this.getImages().stream()
                            .map(img -> img.toEntity())
                            .collect(Collectors.toList())
            );
        }

        return project;
    }

    public static CacheableProject fromProject(Project project) {
        if (project == null) return null;

        CacheableProject cacheableProject = new CacheableProject();
        cacheableProject.setId(project.getId());
        cacheableProject.setName(project.getName());
        cacheableProject.setSlug(project.getSlug());
        cacheableProject.setDescription(project.getDescription());
        cacheableProject.setRepositoryUrl(project.getRepositoryUrl());
        cacheableProject.setStatus(project.getStatus());
        cacheableProject.setStartDate(project.getStartDate());
        cacheableProject.setEndDate(project.getEndDate());

        if (project.getFeatures() != null) {
            Hibernate.initialize(project.getFeatures());
            cacheableProject.setFeatures(new ArrayList<>(project.getFeatures()));
        }

        if (project.getTechnologies() != null) {
            Hibernate.initialize(project.getTechnologies());
            cacheableProject.setTechnologies(new ArrayList<>(project.getTechnologies()));
        }

        if (project.getContributors() != null) {
            Hibernate.initialize(project.getContributors());
            cacheableProject.setContributors(
                    project.getContributors().stream()
                            .map(CacheableContributor::fromContributor)
                            .collect(Collectors.toList())
            );
        }

        if (project.getProjectImages() != null) {
            Hibernate.initialize(project.getProjectImages());
            cacheableProject.setImages(
                    project.getProjectImages().stream()
                            .map(CacheableImage::fromImage)
                            .collect(Collectors.toList())
            );
        }

        return cacheableProject;
    }
}
