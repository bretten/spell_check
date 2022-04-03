package com.brettnamba.spellcheck.services;

import com.brettnamba.spellcheck.repositories.FileWordRepository;
import com.brettnamba.spellcheck.repositories.WordRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashSet;

@SpringBootTest
public class PermutationSpellCheckServiceTests {

    @Test
    void checksWord() {
        // Mock word repo
        WordRepository wordRepository = Mockito.mock(FileWordRepository.class);
        Mockito.when(wordRepository.getAllWords()).thenReturn(new HashSet<>(Arrays.asList("word_1", "word_2", "word_3")));

        // Service
        SpellCheckService s = new PermutationSpellCheckService(wordRepository);

        // Execute
        String word = "word_2";
        SpellCheckService.SpellCheckResult result = s.checkSpelling(word);

        // Assert
        assert result.correct;
        assert result.suggestions.isEmpty();
    }

}
