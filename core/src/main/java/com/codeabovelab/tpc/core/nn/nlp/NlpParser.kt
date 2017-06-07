package com.codeabovelab.tpc.core.nn.nlp

import com.google.common.base.Splitter

/**
 * parser for npltext files
 */
object NlpParser {
    const val EXT = "nlptext"
    private val spaceSplitter = Splitter.on(' ')
    private val pipeSplitter = Splitter.on('|')

    fun parse(line: String, offset: Int = 0): SentenceData {
            val words = ArrayList<WordData>()
            var wo = offset
            spaceSplitter.split(line).forEach {
                words += parseWord(it, wo)
                wo += it.length + 1 /* a space symbol */
            }
            return SentenceData(str = line, offset = offset, words = words)
    }

    private fun parseWord(it: String, wo: Int): WordData {
        val wi = pipeSplitter.split(it).iterator()
        val str = wi.next()
        var lemma: String? = null
        var pos = Pos.UNKNOWN
        while(wi.hasNext()) {
            val kv = wi.next()
            val eqpos = kv.indexOf('=')
            if(eqpos <= 0) {
                continue
            }
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