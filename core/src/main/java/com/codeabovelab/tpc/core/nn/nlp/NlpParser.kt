package com.codeabovelab.tpc.core.nn.nlp

import com.google.common.base.Splitter
import java.io.BufferedReader

/**
 * parser for npltext files
 */
class NlpParser {
    companion object {
        const val EXT = ".nlptext"
        private val spaceSplitter = Splitter.on(' ')
        private val pipeSplitter = Splitter.on('|')
    }

    fun parse(r: BufferedReader): Iterator<SentenceData> {
        return SentenceDataIter(r)
    }

    private class SentenceDataIter(
            private val r: BufferedReader
    ): Iterator<SentenceData> {

        private var sd: SentenceData? = null
        private var offset: Int = 0

        override fun hasNext(): Boolean {
            if(sd != null) {
                return true
            }
            parseLine()
            return sd != null
        }

        override fun next(): SentenceData {
            hasNext()
            if(sd == null) {
                throw NoSuchElementException()
            }
            val tmp = sd!!
            sd = null
            return tmp
        }

        private fun parseLine() {
            val line = r.readLine()
            if(line == null) {
                return
            }
            val words = ArrayList<WordData>()
            var wo = offset
            spaceSplitter.split(line).forEach {
                words += parseWord(it, wo)
                wo += it.length + 1 /* a space symbol */
            }
            sd = SentenceData(str = line, offset = offset, words = words)
            offset += line.length + 1 /* a \n symbol */
        }

        private fun parseWord(it: String, wo: Int): WordData {
            val wi = pipeSplitter.split(it).iterator()
            val str = wi.next()
            var lemma: String? = null
            var pos = Pos.UNKNOWN
            while(wi.hasNext()) {
                val kv = wi.next()
                val eqpos = kv.indexOf('=')
                val k = kv.substring(0, eqpos)
                val v = kv.substring(eqpos + 1)
                when(k) {
                    "p" -> pos = Pos.parse(v)
                    "l" -> lemma = v
                }
            }
            if(lemma == null) {
                lemma = str.toLowerCase()
            }
            return WordData(
                    str = str,
                    offset = wo,
                    lemma = lemma,
                    pos = pos,
                    stem = null
            )
        }
    }
}