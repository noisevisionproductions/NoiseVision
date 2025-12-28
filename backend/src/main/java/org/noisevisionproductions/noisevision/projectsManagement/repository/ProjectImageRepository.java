package org.noisevisionproductions.noisevision.projectsManagement.repository;

import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectImageRepository extends JpaRepository<ImageFromProject, Long> {
}
