package com.codeabovelab.tpc.core.nn.nlp

import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextImpl
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern

/**
 */
class FileTextIterator(private val filePath: Path) : TextIterator {

    companion object {

        private val pattern = Pattern.compile("\\[([^],/\\\\]+)(?:,?([^],/\\\\]+))*\\]")

        fun extractLabels(filePath: Path): List<String> {
            val m = pattern.matcher(filePath.fileName.toString())
            if (m.find()) {
                return (1 until m.groupCount()).map { "#" + m.group(it) }
            } else {
                return Collections.emptyList()
            }
        }
    }
    private var reader: Iterator<String> = Collections.emptyIterator()
    private var id: String = filePath.toAbsolutePath().toString()
    private var _index: Int = 0
    private val _labels: List<String>
    private val stream = Files.lines(filePath)

    init {
        _labels = extractLabels(filePath)
        this.reader = stream.iterator()
    }

    override val count: Int
        get() = -1
    override val labels: List<String>?
        get() = _labels
    override val index: Int
        get() = _index

    override fun hasNext(): Boolean {
        try {
            return reader.hasNext()
        } catch(e: Exception) {
            // it can throw java.nio.channels.ClosedByInterruptException
            return false
        }
    }

    override fun next(): Text {
        val sb = StringBuilder()
        while (hasNext()) {
            val line = reader.next()
            sb.append(line)
            if (line.isEmpty()) {
                break
            }
        }
        return TextImpl("$id#$_index", sb.toString())
    }

    override fun close() {
        stream.close()
    }
}