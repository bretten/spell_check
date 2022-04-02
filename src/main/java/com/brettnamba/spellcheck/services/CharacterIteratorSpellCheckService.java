package com.brettnamba.spellcheck.services;

import com.brettnamba.spellcheck.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@Service
public class CharacterIteratorSpellCheckService implements SpellCheckService {

    @Autowired
    private final WordRepository wordRepository;

    public CharacterIteratorSpellCheckService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public SpellCheckResult CheckSpelling(String word) {
        HashSet<String> words = wordRepository.GetAllWords();

        if (words.contains(word)) {
            return new SpellCheckResult(true, new ArrayList<>());
        }

        return new SpellCheckResult(false, Arrays.asList("wordA", "wordB"));
    }
}
