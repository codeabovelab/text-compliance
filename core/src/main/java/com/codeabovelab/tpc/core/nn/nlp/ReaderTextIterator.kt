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
        r: Reader
) : TextIterator {
    private var reader: Iterator<String> = BufferedReader(r).lines().iterator()
    private var _index: Int = -1

    override val count: Int
        get() = -1
    override val labels: List<String>? = Collections.emptyList()
    override val index: Int
        get() = _index

    override fun hasNext(): Boolean {
        return reader.hasNext()
    }

    override fun next(): Text {
        _index++
        val sb = StringBuilder()
        while (hasNext()) {
            val line = reader.next()
            if (line.isEmpty() || line.isBlank()) {
                break
            }
            sb.append(line).append('\n')
        }
        val data = sb.toString()
        return TextImpl("$id#$_index", data)
    }
}