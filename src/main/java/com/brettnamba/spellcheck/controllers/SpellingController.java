package com.brettnamba.spellcheck.controllers;

import com.brettnamba.spellcheck.services.SpellCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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
     * @return JSON Response. 200 if it was spelled correctly OR if it was misspelled but suggestions were found.
     * Otherwise, 404 if no suggestions were found
     */
    @GetMapping(value = "/{word}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object hello(@PathVariable String word, HttpServletResponse response) {
        SpellCheckService.SpellCheckResult result = spellCheckService.checkSpelling(word);

        // If the result was spelled correctly OR if it was misspelled but had suggestions, then return 200
        if (result.correct || !result.suggestions.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return result;
    }
}
