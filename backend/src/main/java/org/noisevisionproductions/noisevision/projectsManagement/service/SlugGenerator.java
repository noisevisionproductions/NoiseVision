package org.noisevisionproductions.noisevision.projectsManagement.service;

import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
@RequiredArgsConstructor
public class SlugGenerator {
    private final ProjectRepository projectRepository;

    private static final String[][] ADDITIONAL_CHARS = {
            {"ł", "l"}, {"ć", "c"}
    };

    public String generateUniqueSlug(String title) {
        validateTitle(title);
        String normalizedText = normalizeText(title);
        String baseSlug = createBaseSlug(normalizedText);
        return ensureUniqueness(baseSlug);
    }

    private void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
    }

    private String normalizeText(String text) {
        String normalized = text.toLowerCase();
        normalized = replaceSpecialChars(normalized);
        return Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private String replaceSpecialChars(String text) {
        String result = text;
        for (String[] replacement : ADDITIONAL_CHARS) {
            result = result.replace(replacement[0], replacement[1]);
        }
        return result;
    }

    private String createBaseSlug(String normalized) {
        String baseSlug = normalized
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "")
                .trim();

        if (baseSlug.isEmpty()) {
            throw new IllegalArgumentException("Slug cannot be empty after processing");
        }

        return baseSlug;
    }

    private String ensureUniqueness(String baseSlug) {
        String finalSlug = baseSlug;
        int counter = 2;

        while (projectRepository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }

        return finalSlug;
    }
}