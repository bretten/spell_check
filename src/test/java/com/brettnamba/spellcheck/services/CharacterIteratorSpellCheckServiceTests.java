package com.brettnamba.spellcheck.services;

import com.brettnamba.spellcheck.repositories.FileWordRepository;
import com.brettnamba.spellcheck.repositories.WordRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashSet;

@SpringBootTest
public class CharacterIteratorSpellCheckServiceTests {

    @Test
    void checksWord() {
        // Mock word repo
        WordRepository wordRepository = Mockito.mock(FileWordRepository.class);
        Mockito.when(wordRepository.GetAllWords()).thenReturn(new HashSet<>(Arrays.asList("word_1", "word_2", "word_3")));

        // Service
        SpellCheckService s = new CharacterIteratorSpellCheckService(wordRepository);

        // Execute
        String word = "word_2";
        SpellCheckService.SpellCheckResult result = s.CheckSpelling(word);

        // Assert
        assert result.Correct;
        assert result.Suggestions.isEmpty();
    }

}
