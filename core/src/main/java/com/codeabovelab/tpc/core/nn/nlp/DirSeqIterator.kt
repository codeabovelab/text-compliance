package com.codeabovelab.tpc.core.nn.nlp

import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator
import org.deeplearning4j.models.sequencevectors.sequence.Sequence
import org.deeplearning4j.models.word2vec.VocabWord
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream

/**
 * Simple iterator for NN teaching on directory of sample data.
 */
class DirSeqIterator(
        private val dir: String,
        private val wordSupplier: (wc: WordContext) -> String?,
        private val fileSupport: FileSupport
): SequenceIterator<VocabWord> {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private var fileIter: Iterator<Path> = Collections.emptyIterator<Path>()
    private var sentenceIter: SentenceIterator? = null
        set(value) {
            field?.close()
            field = value
        }
    private val seqCounter = AtomicInteger(0)

    init {
        reset()
    }

    override fun nextSequence(): Sequence<VocabWord> {
        var sentence: SentenceData? = null
        while(true) {
            if(sentenceIter == null || !sentenceIter?.hasNext()!!) {
                if(fileIter.hasNext()) {
                    sentenceIter = nextSentenceIter()
                } else {
                    break
                }
            }
            if(sentenceIter != null && sentenceIter?.hasNext()!!) {
                sentence = sentenceIter?.next()
                if(!sentence.isNullOrEmpty()) {
                    break
                }
            }
        }
        //println(sentence)
        if(sentence.isNullOrEmpty()) {
            return Sequence()
        }
        return toSeq(sentence!!)
    }

    private fun toSeq(sentence: SentenceData): Sequence<VocabWord> {
        val sequence = Sequence<VocabWord>()

        val wch = WordContext.create()
        wch.sentence = sentence

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
        labels
                .filterNot { it.isEmpty() }
                .forEach { sequence.addSequenceLabel(VocabWord(1.0, it)) }
        sequence.sequenceId = seqCounter.getAndIncrement()
        return sequence
    }

    private fun nextSentenceIter(): SentenceIterator? {
        if(!fileIter.hasNext()) {
            return null
        }
        val path = fileIter.next()
        log.info("Process {}", path)
        val ext = path.toFile().extension
        val iterSupplier = fileSupport.map[ext] ?: throw IllegalArgumentException("Unsupported file type $path")
        return iterSupplier(path)
    }

    override fun hasMoreSequences(): Boolean {
        return fileIter.hasNext()
    }

    override fun reset() {
        log.warn("Call reset on $dir")
        try {
            val stream = Files.walk(Paths.get(dir))
              .filter {fileSupport.map.containsKey(it.toString().substringAfterLast('.'))}
            this.fileIter = fileSupport.filesIterator(stream)
        } catch (e: IOException) {
            throw IOException("On $dir", e)
        }
    }

    data class FileSupport(
        val map: Map<String, (Path) -> SentenceIterator?>,
        val filesIterator: (Stream<Path>) -> Iterator<Path> = {it.iterator()}
    )
}
