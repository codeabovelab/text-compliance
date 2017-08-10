package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.text.TextImpl
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.google.common.base.Splitter
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.VocabWord
import org.nd4j.linalg.ops.transforms.Transforms
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.text.NumberFormat
import java.util.*
import java.util.stream.Collectors

/**
 * Print list of labels and nearest words
 */
class Evaluate(
        private val inLearned: String,
        private val inData: String?
) {

    fun run() {
        val ld = LearnConfig.learnedDir(inLearned)
        val lc = LearnConfig()
        lc.configure(ld.config)
        val pv = WordVectorSerializer.readParagraphVectors(ld.doc2vec.toFile())

        @Suppress("UNCHECKED_CAST")
        val labels = fieldGet<List<VocabWord>>(pv, "labelsList")
        val nf = NumberFormat.getInstance(Locale.ROOT)
        labels.forEach { label ->
            val labelVec = pv.getWordVectorMatrix(label.word)
            val words = pv.wordsNearest(labelVec, 30).stream()
                    .filter { it != label.word }
                    .map {
                        val wordVec = pv.getWordVectorMatrix(it)
                        val sim = Transforms.cosineSim(labelVec, wordVec)
                        "$it=${nf.format(sim)}"
                    }.collect(Collectors.joining("\n\t"))
            println("${label.word}\n\t$words")
        }
        if (inData != null) {
            val uima = UimaFactory.create(lc.createUimaRequest())
            val wordSupplier = lc.wordSupplier()
            val path = Paths.get(inData)
            println("For file: $path")
            val wch = WordContext.create()
            val sims = StringBuilder()
            Files.lines(path, StandardCharsets.UTF_8).forEach { line ->
                val si = SentenceIteratorImpl.create(uima, TextIterator.singleton(TextImpl(line)))
                while (si.hasNext()) {
                    val sd = si.next()
                    if (sd == null) {
                        continue
                    }
                    println(sd.str)
                    wch.sentence = sd
                    labels.forEach { label ->
                        val labelVec = pv.getWordVectorMatrix(label.word)
                        sd.words.forEach { wd ->
                            wch.word = wd
                            val str = wordSupplier(wch.context)
                            val wordVec = pv.getWordVectorMatrix(str)
                            val sim = if (wordVec == null) {
                                UnkDetector.UNK
                            } else {
                                val tmp = Transforms.cosineSim(labelVec, wordVec)
                                nf.format(tmp)
                            }
                            sims.append("$str=$sim ")
                        }
                        println("${label.word}\n$sims")
                        sims.setLength(0)
                    }
                    println()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> fieldGet(owner: Any, name: String): T {
        val f = owner.javaClass.getDeclaredField(name)
        f.isAccessible = true
        return f.get(owner) as T
    }
}