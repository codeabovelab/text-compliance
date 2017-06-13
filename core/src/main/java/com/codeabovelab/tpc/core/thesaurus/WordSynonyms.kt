package com.codeabovelab.tpc.core.thesaurus

interface WordSynonyms {

    fun lookup(word: String, useCanonical: Boolean = false): SearchResult
}