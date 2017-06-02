package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.nn.nlp.DirSeqIterator
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.io.Resources
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.junit.Test
import org.junit.Ignore
import org.slf4j.LoggerFactory

import java.io.*
import java.nio.charset.StandardCharsets

/**
 */
@Ignore
class TextClassifierTest {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    @Test
    fun test() {
        val workDir = "/home/rad/tmp/nn-data/"
        val filePath = workDir + "ParagraphVectors.zip"


        val tc = TextClassifier(vectorsFile = filePath, maxLabels = 3)

        val texts = Resources.readLines(Resources.getResource(this.javaClass, "samples.txt"), StandardCharsets.UTF_8)
        var i = 0
        val pc = PredicateContext(document = DocumentImpl.builder().id("test_doc").body("<none>").build(), attributes = emptyMap())
        for(text in texts) {
            System.out.println(i++)
            val res = tc.test(pc, TextImpl("sample_" + i, text))
            res.entries.forEach {
                val offset = it.coordinates.offset
                val sentence = text.substring(offset, offset + it.coordinates.length)
                System.out.println(sentence + ": " + it.labels)
            }
        }
    }


}
