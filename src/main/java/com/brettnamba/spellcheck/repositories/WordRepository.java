package com.brettnamba.spellcheck.repositories;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository {

    /**
     * Returns a collection of words
     *
     * @return Collection of words as strings
     */
    List<String> GetAllWords();

}
