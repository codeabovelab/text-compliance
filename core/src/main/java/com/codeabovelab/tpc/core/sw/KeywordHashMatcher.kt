package com.codeabovelab.tpc.core.sw

import com.codeabovelab.tpc.core.nn.nlp.WordContext

class KeywordHashMatcher(val words: Set<String>) : KeywordMatcher {

    override fun test(word: WordContext): Boolean {
        return words.contains(word.word.lemma)
    }
}