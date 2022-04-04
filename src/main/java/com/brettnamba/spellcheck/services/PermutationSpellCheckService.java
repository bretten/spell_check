package com.brettnamba.spellcheck.services;

import com.brettnamba.spellcheck.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        return new SpellCheckResult(false, getSuggestions(word));
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
        // We have already checked if the string has mixed casing, so we can force lowercase here
        word = word.toLowerCase();

        // Iterate over each character and create a collection of all characters. If a character repeats,
        // flag that it repeats
        List<RepeatingCharacter> characters = new ArrayList<>(); // Collection of the characters in order
        char lastChar = 0; // So we know the last character in the next iteration
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);

            if (currentChar != lastChar) {
                // The first time we are seeing this character, so add it to the collection
                characters.add(new RepeatingCharacter(Character.toString(currentChar), false));
            } else {
                // We saw this character last time, so flag the last character in the collection as repeating
                characters.get(characters.size() - 1).repeats = true;
            }

            lastChar = currentChar; // So we know the last character in the next iteration
        }

        // Will hold the collection of unique permutations of the word
        HashSet<String> permutations = new HashSet<>();
        // Find all permutations of the string (the character collection) with and without vowels
        for (char vowel : VOWELS) {
            addPermutationsWithAndWithoutVowels(vowel, "", new RepeatingCharacterCollection(characters), permutations);
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
     * Recursive method to find all permutations of the string with and without vowels before each character.
     * Rather than taking in a string directly as input, the input is a collection of characters with a flag indicating
     * if that character consecutively repeats in the original string.
     * <p>
     * It will iterate over each item in the collection and combine the part that has been iterated over (the prefix)
     * with the part yet to be iterated over (still in the form of a collection). In other words, it will join the
     * collection into a string with and without vowels.
     * <p>
     * Note that we let the vowel only repeat twice due to the assumption that a letter in the English language can't repeat more than twice consecutively
     *
     * @param v                 The current vowel to add
     * @param prefix            The prefix used to append to the joined string (recursively will be each part of the collection, eg: abcd => a,bcd -> ab,cd -> abc,d -> abcd)
     * @param s                 The collection of characters we are iterating over
     * @param vowelPermutations All permutations of the string with and without vowels before each character
     */
    public static void addPermutationsWithAndWithoutVowels(char v, String prefix, RepeatingCharacterCollection s, HashSet<String> vowelPermutations) {
        vowelPermutations.add(prefix + s.toString());
        if (s.parts.size() == 0) {
            return;
        }
        for (char vowel : VOWELS) {
            RepeatingCharacter firstItem = s.getFirstItem();
            String firstCharacter = firstItem != null ? firstItem.c : "";
            // Permutations without any vowels
            addPermutationsWithAndWithoutVowels(vowel, prefix + firstCharacter, s.cloneItemsAfter(1), vowelPermutations);
            // Permutations with a single vowel
            addPermutationsWithAndWithoutVowels(vowel, prefix + v + firstCharacter, s.cloneItemsAfter(1), vowelPermutations);
            // Permutations with the vowel repeating twice
            addPermutationsWithAndWithoutVowels(vowel, prefix + v + v + firstCharacter, s.cloneItemsAfter(1), vowelPermutations);

            RepeatingCharacter firstPart = s.getFirstItem();
            if (firstPart != null && firstPart.repeats) {
                // Permutations if the first character repeats
                addPermutationsWithAndWithoutVowels(vowel, prefix + firstCharacter + firstCharacter, s.cloneItemsAfter(1), vowelPermutations);
                // Permutations if the first character repeats and with a single vowel
                addPermutationsWithAndWithoutVowels(vowel, prefix + v + firstCharacter + firstCharacter, s.cloneItemsAfter(1), vowelPermutations);
                // Permutations if the first character repeats and with the vowel repeating twice
                addPermutationsWithAndWithoutVowels(vowel, prefix + v + v + firstCharacter + firstCharacter, s.cloneItemsAfter(1), vowelPermutations);
            }
        }
    }

    /**
     * Represents a character within a string and a flag indicating it repeats in the string
     */
    private static class RepeatingCharacter {
        public final String c; // Using String rather than character, so we can have empty strings
        public boolean repeats;

        public RepeatingCharacter(String c, boolean repeats) {
            this.c = c;
            this.repeats = repeats;
        }
    }

    /**
     * Represents the characters in a string in order. If the character repeated consecutively, it will not have multiple entries
     * in the collection. Instead, there will just be once occurrence of that character (in order) with a flag
     * indicating it repeats
     */
    private static class RepeatingCharacterCollection {
        public final List<RepeatingCharacter> parts;

        public RepeatingCharacterCollection(List<RepeatingCharacter> parts) {
            this.parts = parts;
        }

        public RepeatingCharacter getFirstItem() {
            if (parts.isEmpty()) {
                return null;
            }
            return parts.get(0);
        }

        /**
         * Clones the items in the collection after the specified index
         */
        public RepeatingCharacterCollection cloneItemsAfter(int index) {
            List<RepeatingCharacter> clone = new ArrayList<>();
            for (int i = index; i < parts.size(); i++) {
                clone.add(parts.get(i));
            }
            return new RepeatingCharacterCollection(clone);
        }

        public String toString() {
            return String.join("", parts.stream().map(a -> a.c).collect(Collectors.joining()));
        }
    }
}
