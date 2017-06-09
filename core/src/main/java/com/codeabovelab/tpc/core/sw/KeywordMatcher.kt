package com.codeabovelab.tpc.core.sw

import com.codeabovelab.tpc.core.nn.nlp.WordContext

/**
 * Test whether word belongs to keyword list
 */
interface KeywordMatcher {

    fun test(word: WordContext): Boolean
}