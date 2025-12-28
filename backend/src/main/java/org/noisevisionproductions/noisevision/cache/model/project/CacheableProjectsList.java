package org.noisevisionproductions.noisevision.cache.model.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
public class CacheableProjectsList implements Serializable {
    private List<CacheableProject> projects = new ArrayList<>();

    public static CacheableProjectsList fromProjects(List<Project> projects) {
        if (projects == null) return null;

        CacheableProjectsList cacheableList = new CacheableProjectsList();
        cacheableList.setProjects(
                projects.stream()
                        .map(CacheableProject::fromProject)
                        .collect(Collectors.toList())
        );
        return cacheableList;
    }

    public List<Project> toEntity() {
        return projects.stream()
                .map(CacheableProject::toEntity)
                .collect(Collectors.toList());
    }
}