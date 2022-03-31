package com.brettnamba.spellcheck.controllers;

import com.brettnamba.spellcheck.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the spell checker
 */
@RestController
@RequestMapping("/spelling")
public class SpellingController {

    @Autowired
    private final WordRepository wordRepository;

    public SpellingController(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    /**
     * Exposes the spell checker service and checks the word in the URL path.
     *
     * @param word The word to check
     * @return
     */
    @GetMapping("/{word}")
    public String hello(@PathVariable String word) {
        String data = String.join(", ", this.wordRepository.GetAllWords());
        return String.format("Word to lookup %s. All words = %s", word, data);
    }
}
