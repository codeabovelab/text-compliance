package com.codeabovelab.tpc.core.thesaurus

class WordSynonyms(val resolver: ThesaurusDictionary.Resolver) {

    fun lookup(word: String): SearchResult {
        return SearchResult(resolver.resolve().lookup(word))
    }

    data class SearchResult(val words: Set<String>)
}