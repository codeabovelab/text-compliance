package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.text.TextImpl
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/**
 */
class Classify(
        private val in_data: String,
        private val in_learned: String
) {
    fun run() {
        val texts = Files.readAllLines(Paths.get(in_data), StandardCharsets.UTF_8)


        val tc = TextClassifier(vectorsFile = in_learned, maxLabels = 3)

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