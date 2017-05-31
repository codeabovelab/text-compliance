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

    private val pattern = Pattern.compile("\\[(\\w+)(?:,?(\\w+))*\\]")
    private var reader: Iterator<String> = Collections.emptyIterator()
    private var id: String = filePath.toAbsolutePath().toString()
    private var _index: Int = 0
    private var _labels: List<String>

    init {
        println("Load $id")
        val m = pattern.matcher(filePath.fileName.toString())
        if (m.find()) {
            _labels = (1 until m.groupCount()).map { m.group(it) }
        } else {
            _labels = Collections.emptyList()
        }
        reset()
    }

    override val count: Int
        get() = -1
    override val labels: List<String>?
        get() = _labels
    override val index: Int
        get() = _index

    override fun reset() {
        this.reader = Files.lines(filePath).iterator()
    }

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
}