package com.codeabovelab.tpc.core.nn.nlp

import org.apache.uima.cas.CAS
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.util.JCasUtil
import org.cleartk.clearnlp.MpAnalyzer
import org.cleartk.clearnlp.PosTagger
import org.cleartk.clearnlp.Tokenizer
import org.cleartk.opennlp.tools.SentenceAnnotator
import org.cleartk.token.type.Sentence
import org.cleartk.token.type.Token
import org.deeplearning4j.text.annotator.TokenizerAnnotator
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator
import org.deeplearning4j.text.uima.UimaResource
import java.util.Collections

/**
 * Based on code from UimaSentenceIterator
 */
class SentenceIteratorImpl(private val iter: TextIterator,
                           private val resource: UimaResource):
        BaseSentenceIterator(null), LabelAwareSentenceIterator {

    private var sentences: Iterator<SentenceData> = Collections.emptyIterator()
    private var curr: SentenceData? = null


    @Synchronized override fun nextSentence(): String? {
        if (!sentences.hasNext()) {
            if(!nextIter()) {
                return null
            }
        }
        return doNext()
    }


    private fun doNext(): String  {
        val sd = sentences.next()
        this.curr = sd
        var ret = sd.str
        val pp = this.getPreProcessor()
        if (pp != null) {
            ret = pp.preProcess(ret)
        }
        return ret
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

    @Synchronized
    override fun hasNext(): Boolean {
        return iter.hasNext() || sentences.hasNext()
    }

    fun currentOffset(): Int? {
        return curr?.offset
    }

    fun current(): SentenceData {
        return curr!!
    }

    companion object {
        fun uimaResource(
                pos: Boolean = true,
                morphological: Boolean = true
        ): UimaResource {
            // note that MpAnalyzer require POS, therefore we must enable both them
            val args = if(morphological) {
                 arrayOf(SentenceAnnotator.getDescription(),
                        Tokenizer.getDescription(),
                        PosTagger.getDescription(),
                        MpAnalyzer.getDescription())
            } else if(pos) {
                arrayOf(SentenceAnnotator.getDescription(),
                        Tokenizer.getDescription(),
                        PosTagger.getDescription())
            } else {
                arrayOf(SentenceAnnotator.getDescription(),
                        Tokenizer.getDescription())
            }
            return UimaResource(AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(*args)))
        }

        /**
         * @see #uimaResource
         */
        fun create(ur: UimaResource, iter: TextIterator): SentenceIteratorImpl {
            return SentenceIteratorImpl(iter, ur)
        }
    }

    override fun reset() {
        iter.reset()
    }

    override fun currentLabel(): String? {
        return null
    }

    override fun currentLabels(): List<String>? {
        return iter.labels
    }
}
