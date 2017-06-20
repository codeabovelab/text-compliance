package com.codeabovelab.tpc.core.kw

import com.codeabovelab.tpc.core.nn.nlp.WordContext

/**
 * Test whether word belongs to keyword list
 */
interface KeywordMatcher {

    /**
     * Return labels set for specified words
     */
    fun test(wc: WordContext): Set<String>
}