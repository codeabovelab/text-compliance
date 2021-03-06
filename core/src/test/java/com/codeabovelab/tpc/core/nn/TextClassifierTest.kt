package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.io.Resources
import org.junit.Test
import org.junit.Ignore
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

/**
 */
@Ignore
class TextClassifierTest {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    @Test
    fun test() {
        val workDir = "/home/rad/tmp/nn-data/"
        val filePath = workDir + "ParagraphVectors.zip"


        val tc = TextClassifier(
                vectorsFile = Paths.get(filePath),
                maxLabels = 3,
                wordSupplier = {it.word.str}
        )

        val texts = Resources.readLines(Resources.getResource(this.javaClass, "samples.txt"), StandardCharsets.UTF_8)
        var i = 0
        val pc = PredicateContext.create(
                sentenceIteratorFactory = SentenceIteratorFactoryImpl(UimaFactory.create(morphological = false, pos = false))
        )
        for(text in texts) {
            System.out.println(i++)
            val res = tc.test(pc, TextImpl(text))
            res.entries.forEach {
                val offset = it.coordinates.offset
                val sentence = text.substring(offset, offset + it.coordinates.length)
                System.out.println(sentence + ": " + it.labels)
            }
        }
    }


}
