package com.codeabovelab.tpc.core.kw

import com.codeabovelab.tpc.core.nn.nlp.WordContext

class KeywordHashMatcher(val words: Set<String>) : KeywordMatcher {

    override fun test(word: WordContext): Boolean {
        return words.contains(word.word.lemma?.toLowerCase())
    }
}