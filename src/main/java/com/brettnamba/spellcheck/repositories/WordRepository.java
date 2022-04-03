package com.brettnamba.spellcheck.repositories;

import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public interface WordRepository {

    /**
     * Returns a collection of words
     *
     * @return Collection of words as strings
     */
    HashSet<String> getAllWords();

}
