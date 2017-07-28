package com.codeabovelab.tpc.core.nn.nlp

import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextImpl
import java.io.BufferedReader
import java.io.Reader
import java.util.*

/**
 */
class ReaderTextIterator(
        private val id: String,
        reader: Reader
) : TextIterator {
    private val buff = BufferedReader(reader)
    private var iterator: Iterator<String> = buff.lines().iterator()
    private var _index: Int = -1

    override val count: Int
        get() = -1
    override val labels: List<String>? = Collections.emptyList()
    override val index: Int
        get() = _index

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun next(): Text {
        _index++
        val sb = StringBuilder()
        while (hasNext()) {
            val line = iterator.next()
            if (line.isEmpty() || line.isBlank()) {
                break
            }
            sb.append(line).append('\n')
        }
        val data = sb.toString()
        return TextImpl(data)
    }

    override fun close() {
        buff.close()
    }
}