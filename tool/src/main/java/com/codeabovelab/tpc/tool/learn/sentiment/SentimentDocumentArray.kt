package com.codeabovelab.tpc.tool.learn.sentiment

interface SentimentDocumentArray {

    fun size(): Int
    operator fun get(cursor: Int): SentimentDocument
    fun reset()
}