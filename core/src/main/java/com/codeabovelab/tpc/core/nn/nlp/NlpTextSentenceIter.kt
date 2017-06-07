package com.codeabovelab.tpc.core.nn.nlp

import java.nio.file.Files
import java.nio.file.Path

/**
 */
class NlpTextSentenceIter(
        private val iter: Iterator<String>,
        private val labels: List<String>?
) : SentenceIterator {

    private var offset = 0

    override fun hasNext(): Boolean {
        return iter.hasNext()
    }

    override fun next(): SentenceData? {
        val line = iter.next()
        val sd = NlpParser.parse(line, offset)
        offset += line.length + 1 /*  a \n symbol */
        return sd
    }


    override fun currentLabels(): List<String>? {
        return labels
    }

    companion object {
        fun create(path: Path): SentenceIterator {
            val iter = Files.lines(path).iterator()
            return NlpTextSentenceIter(
                    iter = iter,
                    labels = FileTextIterator.extractLabels(path)
            )
        }
    }
}