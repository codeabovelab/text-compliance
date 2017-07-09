package com.codeabovelab.tpc.tool.learn

interface SentimentDocumentArray {

    fun size(): Int
    operator fun get(cursor: Int): SentimentDocument
    fun reset()
}