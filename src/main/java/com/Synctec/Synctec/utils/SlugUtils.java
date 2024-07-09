package com.Synctec.Synctec.utils;

import com.Synctec.Synctec.repository.PostRepository;
import com.Synctec.Synctec.service.interfaces.JpaInterfaces.PostJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A Utility class that provides function for generating unique slugs used by the post entity)
 * over the network .
 * @author Chukwudile .I. David.
 */
@RequiredArgsConstructor
@Service
public class SlugUtils {

    private final PostJpaService postJpaService;
    private final PostRepository postRepository;
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        // Convert input to lower case and normalize (remove accents)
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");

        return slug.toLowerCase(Locale.ENGLISH);
    }

    public  String findUniqueSlug(String slug) {
        String uniqueSlug = slug;
        int counter = 1;
        // Check if the slug already exists in the database
        while (postRepository.findBySlug(uniqueSlug).isPresent()) {
            uniqueSlug = slug + "-" + counter;
            counter++;
        }
        return uniqueSlug;

    }
}
