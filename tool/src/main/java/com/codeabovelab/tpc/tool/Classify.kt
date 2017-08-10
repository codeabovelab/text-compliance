package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.text.TextImpl
import com.codeabovelab.tpc.tool.learn.LearnConfig
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/**
 */
class Classify(
        private val inData: String,
        private val inLearned: String
) {
    fun run() {
        val texts = Files.readAllLines(Paths.get(inData), StandardCharsets.UTF_8)
        val ld = LearnConfig.learnedDir(inLearned)
        val lc = LearnConfig()
        lc.configure(ld.config)
        val tc = TextClassifier(
                vectorsFile = ld.doc2vec,
                maxLabels = 3,
                wordSupplier = lc.wordSupplier()
        )

        var i = 0
        val pc = PredicateContext.create(
                sentenceIteratorFactory = SentenceIteratorFactoryImpl(UimaFactory.create(lc.createUimaRequest()))
        )
        for(text in texts) {
            i++
            println("Text #$i")
            val res = tc.test(pc, TextImpl(text))
            res.entries.forEach {
                val offset = it.coordinates.offset
                val sentence = text.substring(offset, offset + it.coordinates.length)
                System.out.println(sentence + "\n\t" + it.labels)
            }
        }

    }
}