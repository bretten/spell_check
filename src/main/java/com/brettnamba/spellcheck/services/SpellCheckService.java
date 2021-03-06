package com.brettnamba.spellcheck.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * Service to perform spell checking
 */
@Service
public interface SpellCheckService {

    /**
     * Should check the spelling of the input word
     * <p>
     * If the word is correctly spelled, it will return a result indicating success.
     * <p>
     * If the word is not correctly spelled, it will attempt to find suggestions based on the input word. If suggestions
     * could be found, it will return a flag indicating the input word was not correctly spelled, and offer the
     * suggestions.
     * If NO suggestions could be found, it will return a failure.
     *
     * @param word The word to check the spelling of
     * @return A result object indicating if the spelling was correct and possible spelling suggestions if it was not
     */
    SpellCheckResult checkSpelling(String word);

    /**
     * Result that any implementation of SpellCheckService should use as the output of a spell check
     */
    class SpellCheckResult {
        /**
         * True if the input word was spelled correctly, otherwise false
         */
        public boolean correct;

        /**
         * If the input word was not spelled correctly, the service will attempt to offer suggestions.
         * <p>
         * If no suggestions could be found, this should be left empty.
         */
        public HashSet<String> suggestions;

        public SpellCheckResult(boolean correct, HashSet<String> suggestions) {
            this.correct = correct;
            this.suggestions = suggestions;
        }
    }

}
