package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextImpl
import org.apache.commons.io.IOUtils
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.charset.StandardCharsets

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
            val f = path.toFile()
            val fis = FileInputStream(f)
            val str = IOUtils.toString(fis, StandardCharsets.UTF_8)

            //TODO use stream
            return SentenceIteratorImpl.create(TextIterator.singleton(TextImpl(f.canonicalPath, str)), pos = false)
        } catch (e: IOException) {
            throw RuntimeException("On read " + path,  e)
        }
    }

    override fun hasNext(): Boolean {
        return fileIter.hasNext()
    }

    override fun reset() {
        try {
            val stream = Files.walk(Paths.get(dir))
              .filter{ it.toString().endsWith(".txt") }
            this.fileIter = stream.iterator()
        } catch (e: IOException) {
            log.error("On {}", dir, e)
        }
    }
}
