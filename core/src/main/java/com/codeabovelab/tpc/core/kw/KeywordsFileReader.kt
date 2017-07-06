package com.codeabovelab.tpc.core.kw

import java.io.BufferedReader
import java.io.Reader

/**
 * Keywords File contains JSON header, delimiter empty line and unlimited count of lines, each of
 * ones contains single keyword or phrase
 */
class KeywordsFileReader(
        private val deserializer: KeywordsFileHeaderDeserializer
) {
    companion object {
        /**
         * Field extension
         */
        const val EXT = "keywords"

    }

    fun read(reader: Reader, onKeyword: KeywordsHandler) {
        BufferedReader(reader).use { br ->
            val str = readHeader(br)
            val kfh = deserializer(str)
            while (true) {
                val line = br.readLine()
                if (line == null) {
                    break
                }
                onKeyword(kfh, line)
            }
        }
    }

    private fun readHeader(br: BufferedReader): String {
        val sb = StringBuilder()
        while (true) {
            val line = br.readLine()
            if (line.isNullOrBlank()) {
                break
            }
            sb.appendln(line)
        }
        val headerString = sb.toString()
        return headerString
    }
}

typealias KeywordsFileHeaderDeserializer = (String) -> KeywordsFileHeader
typealias KeywordsHandler = (KeywordsFileHeader, String) -> Unit