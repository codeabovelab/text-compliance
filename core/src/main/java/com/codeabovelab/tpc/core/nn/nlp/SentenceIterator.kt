package com.codeabovelab.tpc.core.nn.nlp

/**
 */
interface SentenceIterator: AutoCloseable {
    fun hasNext(): Boolean
    fun next(): SentenceData?
    fun currentLabels(): List<String>?
}