package com.codeabovelab.tpc.core.nn.nlp

import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator
import org.deeplearning4j.models.sequencevectors.sequence.Sequence
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.text.uima.UimaResource
import org.slf4j.LoggerFactory
import java.io.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 * Simple iterator for NN teaching on directory of sample data.
 */
class DirSeqIterator(
        private val ur: UimaResource,
        private val dir: String,
        private val wordSupplier: (wc: WordContext) -> String?
): SequenceIterator<VocabWord> {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var fileIter: Iterator<Path> = Collections.emptyIterator<Path>()
    private var sentenceIter: SentenceIteratorImpl? = null
    private val seqCounter = AtomicInteger(0)

    init {
        reset()
    }

    override fun nextSequence(): Sequence<VocabWord> {
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
        if(sentence == null) {
            return Sequence()
        }
        return toSeq()
    }

    private fun toSeq(): Sequence<VocabWord> {
        val sequence = Sequence<VocabWord>()

        val wch = WordContext.create()
        wch.sentence = sentenceIter?.current()

        for (token in wch.sentence!!.words) {
            wch.word = token
            val str = wordSupplier(wch.context)
            if(str.isNullOrBlank()) {
                continue
            }
            val word = VocabWord(1.0, str!!)
            sequence.addElement(word)
        }
        val labels = sentenceIter?.currentLabels() ?: Collections.emptyList()
        for (label in labels) {
            if (label.isEmpty()) {
                continue
            }
            sequence.addSequenceLabel(VocabWord(1.0, label))
        }
        sequence.sequenceId = seqCounter.getAndIncrement()
        return sequence
    }

    private fun nextSentenceIter(): SentenceIteratorImpl? {
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

    override fun hasMoreSequences(): Boolean {
        return fileIter.hasNext()
    }

    override fun reset() {
        log.warn("Call reset on $dir")
        try {
            val stream = Files.walk(Paths.get(dir))
              .filter{ it.toString().endsWith(".txt") }.sorted()
            this.fileIter = stream.iterator()
        } catch (e: IOException) {
            log.error("On {}", dir, e)
        }
    }

}
