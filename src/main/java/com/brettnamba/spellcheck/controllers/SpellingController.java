package com.brettnamba.spellcheck.controllers;

import com.brettnamba.spellcheck.services.SpellCheckService;
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

    /**
     * Service to check the spelling of a word and return suggestions if it is misspelled.
     */
    @Autowired
    private final SpellCheckService spellCheckService;

    /**
     * Constructor
     *
     * @param spellCheckService Service to check the spelling of a word and return suggestions if it is misspelled.
     */
    public SpellingController(SpellCheckService spellCheckService) {
        this.spellCheckService = spellCheckService;
    }

    /**
     * Exposes the spell checker service and checks the word in the URL path.
     *
     * @param word The word to check
     * @return
     */
    @GetMapping("/{word}")
    public String hello(@PathVariable String word) {
        SpellCheckService.SpellCheckResult result = spellCheckService.checkSpelling(word);

        // TODO JSON response. Response status code 200 on success, 404 on word not found
        return String.format("Word to lookup %s. Success = %s, Suggestions = %s",
                word, result.Correct, String.join(", ", result.Suggestions));
    }
}
