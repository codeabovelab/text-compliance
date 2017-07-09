package com.codeabovelab.tpc.tool.learn

class SentimentDocument(
        val text: String,
        val label: SentimentLabel
)

enum class SentimentLabel {
    POSITIVE, NEGATIVE
}