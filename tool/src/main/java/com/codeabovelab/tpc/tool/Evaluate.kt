package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.tool.learn.LearnConfig
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.VocabWord
import org.nd4j.linalg.ops.transforms.Transforms
import java.text.NumberFormat
import java.util.*
import java.util.stream.Collectors

/**
 * Print list of labels and nearest words
 */
class Evaluate(
        private val inLearned: String
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
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> fieldGet(owner: Any, name: String): T {
        val f = owner.javaClass.getDeclaredField(name)
        f.isAccessible = true
        return f.get(owner) as T
    }
}