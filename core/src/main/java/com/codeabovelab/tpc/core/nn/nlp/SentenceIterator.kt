package com.codeabovelab.tpc.core.nn.nlp

/**
 */
interface SentenceIterator {
    fun hasNext(): Boolean
    fun next(): SentenceData?
    fun currentLabels(): List<String>?
}