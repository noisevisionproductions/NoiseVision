package org.noisevisionproductions.noisevision.cache.model.project;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.noisevisionproductions.noisevision.projectsManagement.model.Contributor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CacheableContributor implements Serializable {
    private String name;
    private String role;
    private String profileUrl;

    public static CacheableContributor fromContributor(Contributor contributor) {
        if (contributor == null) return null;

        CacheableContributor cacheable = new CacheableContributor();
        cacheable.setName(contributor.getName());
        cacheable.setRole(contributor.getRole());
        cacheable.setProfileUrl(contributor.getProfileUrl());
        return cacheable;
    }

    public Contributor toEntity() {
        Contributor contributor = new Contributor();
        contributor.setName(this.getName());
        contributor.setRole(this.getRole());
        contributor.setProfileUrl(this.getProfileUrl());
        return contributor;
    }
}
