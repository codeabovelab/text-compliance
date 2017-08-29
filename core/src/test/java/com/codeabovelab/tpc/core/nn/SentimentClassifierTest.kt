package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.text.TextImpl
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths

class SentimentClassifierTest {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    @Ignore
    fun test() {
        val modelFile = Paths.get("/home/pronto/Downloads/sentiment/output/res.zip")
        val wordVectorFile = Paths.get("/home/pronto/Downloads/sentiment/vectors-negative300.bin/vectors.bin")
        val classifier = SentimentClassifier(modelFile = modelFile, wordVectorFile = wordVectorFile)

        val result = classifier.test(PredicateContext.create(), TextImpl(
                "This is a terrible soft, don't waste your money on it. Don't even watch it for free. That's all I have to say."))
        log.info("result {}", result)
        val labels = result.labels
        assertTrue(labels.isNotEmpty())
    }

}