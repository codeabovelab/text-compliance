package com.codeabovelab.tpc.tool.learn.sentiment

class SentimentDocument(
        val text: String,
        val label: SentimentLabel
)

enum class SentimentLabel {
    POSITIVE, NEGATIVE
}