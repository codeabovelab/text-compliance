package com.codeabovelab.tpc.core.nn.nlp

import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.cleartk.opennlp.tools.SentenceAnnotator
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator
import org.deeplearning4j.text.uima.UimaResource
import org.slf4j.LoggerFactory
import java.io.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Collections

/**
 * Simple iterator for NN teaching on directory of sample data.
 */
class DirSentenceIterator (
        private val dir: String
): BaseSentenceIterator(), LabelAwareSentenceIterator {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var fileIter: Iterator<Path> = Collections.emptyIterator<Path>()
    private var sentenceIter: LabelAwareSentenceIterator? = null
    private var labels: List<String> = Collections.emptyList()
    private val ur = UimaResource(AnalysisEngineFactory.createEngine(AnalysisEngineFactory
        .createEngineDescription(
                SentenceAnnotator.getDescription()
        )
    ))

    init {
        reset()
    }

    override fun currentLabel(): String? {
        return null
    }


    override fun currentLabels(): List<String> {
        return labels
    }

    override fun nextSentence(): String? {
        var sentence: String? = null
        while(true) {
            if(sentenceIter == null || !sentenceIter?.hasNext()!!) {
                if(fileIter.hasNext()) {
                    sentenceIter = nextSentenceIter()
                } else {
                    break
                }
            }
            if(sentenceIter != null && sentenceIter?.hasNext()!!) {
                sentence = sentenceIter?.nextSentence()
                if(sentence != null) {
                    break
                }
            }
        }
        //println(sentence)
        return sentence
    }

    private fun nextSentenceIter(): LabelAwareSentenceIterator? {
        if(!fileIter.hasNext()) {
            return null
        }
        val path = fileIter.next()
        try {
            return SentenceIteratorImpl.create(ur, FileTextIterator(path))
        } catch (e: IOException) {
            throw RuntimeException("On read " + path,  e)
        }
    }

    override fun hasNext(): Boolean {
        return fileIter.hasNext()
    }

    override fun reset() {
        log.warn("Call reset on $dir")
        try {
            val stream = Files.walk(Paths.get(dir))
              .filter{ it.toString().endsWith(".txt") }
            this.fileIter = stream.iterator()
        } catch (e: IOException) {
            log.error("On {}", dir, e)
        }
    }
}
