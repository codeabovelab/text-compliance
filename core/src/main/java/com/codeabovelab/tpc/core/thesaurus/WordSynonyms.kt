package com.codeabovelab.tpc.core.thesaurus

class WordSynonyms(val dict: ThesaurusDictionary) {

    fun lookup(word: String): SearchResult {
        return SearchResult(dict.lookup(word))
    }

    data class SearchResult(val words: Set<String>)
}