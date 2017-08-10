package com.codeabovelab.tpc.core.nn.nlp

import org.apache.uima.cas.CAS
import org.apache.uima.fit.util.JCasUtil
import org.cleartk.token.type.Sentence
import org.cleartk.token.type.Token
import org.deeplearning4j.text.uima.UimaResource
import java.util.Collections

/**
 * Based on code from UimaSentenceIterator
 */
class SentenceIteratorImpl(
        private val iter: TextIterator,
        private val resource: UimaResource
): SentenceIterator {

    private var sentences: Iterator<SentenceData> = Collections.emptyIterator()


    override fun next(): SentenceData? {
        if (!sentences.hasNext()) {
            if(!nextIter()) {
                return null
            }
        }
        return sentences.next()
    }

    private fun nextIter(): Boolean {
        //TODO improve this
        if (!iter.hasNext()) {
            return false
        }
        val cas = resource.retrieve()
        try {
            sentences = getSentences(cas)
            while (!sentences.hasNext()) {
                if (!iter.hasNext()) {
                    return false
                }
                cas.reset()
                sentences = getSentences(cas)
            }
        } finally {
            resource.release(cas)
        }
        return true
    }

    private fun getSentences(cas: CAS) : Iterator<SentenceData> {

        val jCas = cas.jCas
        val text = iter.next()
        // set the document's text
        jCas.documentText = text.data.toString()

        resource.analysisEngine.process(cas)
        val list = ArrayList<SentenceData>()
        JCasUtil.select(cas.jCas, Sentence::class.java).mapTo(list) {
            val words = JCasUtil.selectCovered(cas.jCas, Token::class.java, it).map {
                WordData(it.coveredText, it.begin,
                        pos = Pos.parse(it.pos),
                        stem = it.stem,
                        lemma = it.lemma
                )
            }
            SentenceData(it.coveredText, it.begin, words)
        }
        return list.iterator()
    }

    override fun hasNext(): Boolean {
        return iter.hasNext() || sentences.hasNext()
    }

    companion object {


        /**
         * @see #uimaResource
         */
        fun create(ur: UimaResource, iter: TextIterator): SentenceIterator {
            return SentenceIteratorImpl(iter, ur)
        }
    }

    override fun currentLabels(): List<String>? {
        return iter.labels
    }

    override fun close() {
        if(iter is AutoCloseable) {
            iter.close()
        }
    }
}
