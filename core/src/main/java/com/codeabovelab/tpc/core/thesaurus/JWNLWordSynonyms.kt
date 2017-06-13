package com.codeabovelab.tpc.core.thesaurus

import net.didion.jwnl.dictionary.Dictionary

class JWNLWordSynonyms : WordSynonyms {

    override fun lookup(word: String, useCanonical: Boolean): SearchResult {
        val allIndexWords = Dictionary.getInstance().lookupAllIndexWords(word)
        val words = allIndexWords.indexWordArray
                .flatMap { it.senses.asIterable() }
                .flatMap { it.words.asIterable() }
                .map { it.lemma.toLowerCase() }
                .filter { !it.contains("_") }
                .toHashSet()
        return SearchResult(words)
    }
}