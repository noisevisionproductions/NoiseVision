package org.noisevisionproductions.portfolio.cache.model.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.projectsManagement.model.Contributor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CacheableContributorTest {

    @Test
    void shouldCorrectlyConvertFromContributorToCacheableContributor() {
        Contributor contributor = new Contributor();
        contributor.setName("John Doe");
        contributor.setRole("Frontend Developer");
        contributor.setProfileUrl("https://github.com/johndoe");

        CacheableContributor cacheableContributor = CacheableContributor.fromContributor(contributor);

        assertNotNull(cacheableContributor);
        assertEquals("John Doe", cacheableContributor.getName());
        assertEquals("Frontend Developer", cacheableContributor.getRole());
        assertEquals("https://github.com/johndoe", cacheableContributor.getProfileUrl());
    }

    @Test
    void shouldCorrectlyConvertCacheableContributorToContributor() {
        CacheableContributor cacheableContributor = new CacheableContributor();
        cacheableContributor.setName("Jane Smith");
        cacheableContributor.setRole("Backend Developer");
        cacheableContributor.setProfileUrl("https://github.com/janesmith");

        Contributor contributor = cacheableContributor.toEntity();

        assertNotNull(contributor);
        assertEquals("Jane Smith", contributor.getName());
        assertEquals("Backend Developer", contributor.getRole());
        assertEquals("https://github.com/janesmith", contributor.getProfileUrl());
    }

    @Test
    void shouldCreateContributorWithEmptyFields() {
        CacheableContributor cacheableContributor = new CacheableContributor();

        Contributor contributor = cacheableContributor.toEntity();

        assertNotNull(contributor);
        assertNull(contributor.getName());
        assertNull(contributor.getRole());
        assertNull(contributor.getProfileUrl());
    }

    @Test
    void shouldCorrectlyUseAllArgsConstructor() {
        CacheableContributor cacheableContributor = new CacheableContributor();
        String name = "Alice Johnson";
        String role = "DevOps Engineer";
        String profileUrl = "https://github.com/alicejohnson";

        cacheableContributor.setName(name);
        cacheableContributor.setRole(role);
        cacheableContributor.setProfileUrl(profileUrl);

        assertEquals(name, cacheableContributor.getName());
        assertEquals(role, cacheableContributor.getRole());
        assertEquals(profileUrl, cacheableContributor.getProfileUrl());
    }

    @Test
    void shouldHandlePartiallyFilledContributor() {
        Contributor contributor = new Contributor();
        contributor.setName("Bob Wilson");

        CacheableContributor cacheableContributor = CacheableContributor.fromContributor(contributor);

        assertNotNull(cacheableContributor);
        assertEquals("Bob Wilson", cacheableContributor.getName());
        assertNull(cacheableContributor.getRole());
        assertNull(cacheableContributor.getProfileUrl());
    }
}