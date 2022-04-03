package com.brettnamba.spellcheck.services;

import com.brettnamba.spellcheck.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Given a word, will get all permutations of the word made possible by repeating characters AND missing vowels.
 * It will check each permutation of the word and if it exists in the word dictionary, it will be returned as a suggestion
 * if the original word was not spelled correctly.
 */
@Service
public class PermutationSpellCheckService implements SpellCheckService {

    /**
     * The source of the words
     */
    @Autowired
    private final WordRepository wordRepository;

    /**
     * The dictionary of valid words
     * <p>
     * HashSet allows fast lookup by hash for large collections and prevents duplicates
     */
    private HashSet<String> dictionary;

    /**
     * Vowels
     */
    private static final char[] VOWELS = new char[]{'a', 'e', 'i', 'o', 'u'};

    public PermutationSpellCheckService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    /**
     * Checks the spelling of the word and sees if it exists in the dictionary of words.
     *
     * @param word The word to check the spelling of
     * @return The spelling result, showing if the spelling was correct and any suggestions if it wasn't
     */
    @Override
    public SpellCheckResult checkSpelling(String word) {
        // The dictionary of valid words
        if (this.dictionary == null || this.dictionary.isEmpty()) {
            this.dictionary = this.wordRepository.getAllWords();
        }

        // If the word is mixed case, it is considered misspelled, so look for suggestions
        if (isMixedCase(word)) {
            return new SpellCheckResult(false, getSuggestions(word.toLowerCase()));
        }

        // We checked for mixed casing above. toLowerCase() here handles valid cases like Hello and HELLO, so we can check against the dictionary
        if (dictionary.contains(word.toLowerCase())) {
            return new SpellCheckResult(true, new HashSet<>());
        }

        // The word was not found in the dictionary, so look for suggestions
        return new SpellCheckResult(false, getSuggestions(word.toLowerCase()));
    }

    /**
     * Determines if a word has mixed casing.
     * <p>
     * Hello (first letter capital) and HELLO (all letters capital) are NOT considered mixed casing.
     *
     * @param word The word to check for mixed casing
     * @return True if there is mixed casing, otherwise false
     */
    private boolean isMixedCase(String word) {
        if (word.length() <= 1) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        // Don't need to check the first character since "Hello" is not considered mixed casing
        for (int i = 1; i < word.length(); i++) {
            if (!hasUpperCase && Character.isUpperCase(word.charAt(i))) {
                hasUpperCase = true;
            }
            if (!hasLowerCase && Character.isLowerCase(word.charAt(i))) {
                hasLowerCase = true;
            }

            if (hasUpperCase && hasLowerCase) {
                return true;
            }
        }

        // Only one of the uppercase/lowercase flags was true
        return false;
    }

    /**
     * Gets suggestions by finding all permutations of a word that exist within the dictionary of words
     *
     * @param word The word to get suggestions for
     * @return A collection of suggestion words
     */
    private HashSet<String> getSuggestions(String word) {
        // Get all the possible permutations of the word using repeating characters and vowels
        HashSet<String> allPermutations = getAllPermutationsOfWord(word);

        // Check each permutation to see if it exists in our word dictionary
        HashSet<String> suggestions = new HashSet<>();
        for (String permutation : allPermutations) {
            if (this.dictionary.contains(permutation)) {
                // The word exists in the dictionary, so it is a valid suggestion
                suggestions.add(permutation);
            }
        }

        return suggestions;
    }

    /**
     * Given a word, this will get all permutations of the word made possible by repeating characters AND missing vowels.
     * This is accomplished by:
     * <p>
     * 1) Gets all permutations of repeating characters with a limit of repeating twice. For example, if given the
     * word "ballloooon", the possible permutations will be:
     * balloon
     * ballon
     * baloon
     * balon
     * A character shouldn't repeat more than twice in the English language. If this is a wrong assumption, we can
     * further add permutations past repeating a character twice (but we wouldn't know what the limit would be. It
     * would approach infinity).
     * <p>
     * 2) Add all vowels between each character, so we can take into account all missing vowels. We need every possible
     * permutation of each vowel between EACH character in the word.
     *
     * @param word The word to get all permutations for
     * @return A collection of all permutations of the word
     */
    private HashSet<String> getAllPermutationsOfWord(String word) {
        // Iterate over each character and find the indexes where a character repeats
        List<Integer> repeatIndexes = new ArrayList<>(); // Indexes of any repeating characters
        StringBuilder b = new StringBuilder(); // Will hold the word, but with no repeating characters
        char lastChar = 0; // So we know the last character in the next iteration
        boolean recordedRepeatingIndex = false; // Flag to indicate we are currently iterating over repeating characters
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);

            // Record the index of the repeating character
            if (lastChar == currentChar && !recordedRepeatingIndex) { // We are on the second character of a repeating character sequence
                repeatIndexes.add(b.length() - 1); // The beginning of the repeating character sequence was the last char
                recordedRepeatingIndex = true; // Indicate we have recorded the index
                continue;
            } else if (lastChar == currentChar) { // We are on the 2 + n character of a repeating character sequence
                continue; // No action needed
            }

            recordedRepeatingIndex = false; // The repeating character sequence has stopped, so reset the flag

            b.append(currentChar); // Add each character from the word to the builder (won't record repeating characters)

            lastChar = currentChar; // So we know the last character in the next iteration
        }

        // Will hold the collection of unique permutations of the word
        HashSet<String> permutations = new HashSet<>();
        // Add all permutations of the word as is (without repeating characters)
        permutations.addAll(getAllPermutationsWithAndWithoutVowels(b.toString()));

        // Iterate over the StringBuilder (which has no repeating chars)
        for (int i = 0; i < b.length(); i++) {
            char c = b.charAt(i); // The current character
            boolean isRepeatingCharacter = repeatIndexes.contains(i); // Was this character repeating in the original word?

            // If the character repeats, add all permutations of it repeating
            if (isRepeatingCharacter) {
                // Add all permutations with the character repeated twice (we won't exceed repeating twice -- see method comment)
                permutations.addAll(getAllPermutationsWithAndWithoutVowels(b.substring(0, i) + c + b.substring(i, b.length())));
                // We also need permutations of the repeating character with other repeating characters
                // TODO: Can this be done recursively?
                for (int fr : repeatIndexes) { // Check all the other repeating characters (the indexes of them)
                    if (fr <= i) { // We can skip over any repeating characters before the current character
                        continue;
                    }
                    // Add all permutations with the character repeated twice (along with the permutations of the other repeating characters limited to 2)
                    permutations.addAll(getAllPermutationsWithAndWithoutVowels(b.substring(0, i) + b.substring(i, fr) + b.charAt(fr) + b.substring(fr, b.length())));
                    permutations.addAll(getAllPermutationsWithAndWithoutVowels(b.substring(0, i) + c + b.substring(i, fr) + b.charAt(fr) + b.substring(fr, b.length())));
                }
            }
        }

        // The above method did not add vowels to the end, so we need to consider missing vowels on the end of each permutation
        HashSet<String> permutationsEndingWithVowels = new HashSet<>();
        for (String permutation : permutations) {
            for (char vowel : VOWELS) {
                permutationsEndingWithVowels.add(permutation + vowel);
            }
        }
        permutations.addAll(permutationsEndingWithVowels); // Merge the permutations

        return permutations;
    }

    /**
     * Given a string, will get all permutations of the string without vowels and with each vowel before each character
     * <p>
     * Given the string "www", it will return the following:
     * (a)www, w(a)ww, ww(a) (and the rest for the other vowels)
     * TODO: But it still needs to do every permutation of every vowel, such as:
     * (a)w(a)ww, (a)w(e)ww, (a)w(i)ww, (a)w(o)ww, (a)w(u)ww, ..., and so on
     * <p>
     * TODO: This can be handled with a recursive method
     *
     * @param s The string to get all permutations with and without vowels
     * @return A collection of vowel permutations
     */
    private List<String> getAllPermutationsWithAndWithoutVowels(String s) {
        List<String> vowelPermutations = new ArrayList<>();
        vowelPermutations.add(s); // Add the case without any vowels

        for (int i = 0; i < s.length(); i++) {
            for (char vowel : VOWELS) {
                vowelPermutations.add(s.substring(0, i) + vowel + s.substring(i));
            }
        }
        return vowelPermutations;
    }
}
