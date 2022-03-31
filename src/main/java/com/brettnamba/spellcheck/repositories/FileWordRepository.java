package com.brettnamba.spellcheck.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses a text file as a data source
 * <p>
 * In the real world, other implementations of WordRepository might use a database or some cloud service
 */
@Repository
public class FileWordRepository implements WordRepository {

    /**
     * Path to the file containing the words
     */
    @Value("${repositories.filewordrepository.path}")
    private String path;

    /**
     * The words loaded from the file
     */
    private List<String> words;

    /**
     * Returns all words using a file as a data source
     *
     * @return Collection of all words as strings
     */
    @Override
    public List<String> GetAllWords() {
        if (words == null || words.isEmpty()) {
            LoadWords(); // Word list is static, so lazy load
        }
        return words;
    }

    /**
     * Loads words from the configured file path
     */
    private void LoadWords() {
        // Did not work
//        try {
//            // Read the file configured by the path variable to load the words
//            File file = ResourceUtils.getFile(String.format("%s", path));
//            words = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        words = new ArrayList<>();
        Resource resource = new ClassPathResource(path);
        try {
            InputStream resourceInputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceInputStream));
            while (reader.ready()) {
                words.add(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Let exception bubble up
        }
    }
}
