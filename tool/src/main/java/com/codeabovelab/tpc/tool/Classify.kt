package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.doc.DocumentImpl
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
                uima = lc.createUimaResource(),
                wordSupplier = lc.wordSupplier()
        )

        var i = 0
        val pc = PredicateContext(document = DocumentImpl.Companion.builder().id("test_doc").body("<none>").build(), attributes = emptyMap())
        for(text in texts) {
            i++
            println("Text #$i")
            val res = tc.test(pc, TextImpl("sample_$i", text))
            res.entries.forEach {
                val offset = it.coordinates.offset
                val sentence = text.substring(offset, offset + it.coordinates.length)
                System.out.println(sentence + "\n\t" + it.labels)
            }
        }

    }
}