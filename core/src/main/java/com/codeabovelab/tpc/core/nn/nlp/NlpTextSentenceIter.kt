package com.codeabovelab.tpc.core.nn.nlp

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 */
class NlpTextSentenceIter private constructor(
        private val stream: Stream<String>,
        private val labels: List<String>?
) : SentenceIterator, AutoCloseable {

    private val iter = stream.iterator();
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

    override fun close() {
        stream.close()
    }

    companion object {
        fun create(path: Path): SentenceIterator {
            val stream = Files.lines(path)
            return NlpTextSentenceIter(
                    stream = stream,
                    labels = FileTextIterator.extractLabels(path)
            )
        }
    }
}