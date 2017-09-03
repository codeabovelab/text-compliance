package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.nn.sentiment.SentimentClassifier
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.text.TextImpl
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.test.assertFalse

class SentimentClassifierTest {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    @Ignore
    fun test() {
        val modelFile = Paths.get("/home/pronto/Downloads/sentiment/output/res_old.zip")
        val wordVectorFile = Paths.get("/home/pronto/Downloads/sentiment/sentiment_vectors.bin")
        val classifier = SentimentClassifier(modelFile = modelFile, wordVectorFile = wordVectorFile)

        val negative = classifier.test(PredicateContext.create(), TextImpl(
                "This is a terrible software, don't waste your money on it. Don't even watch it for free. That's all I have to say."))
        log.info("result {}", negative)
        assertFalse(negative.labels.isEmpty())

        val positive = classifier.test(PredicateContext.create(), TextImpl(
                "App works great! Especially when I get a new phone and need to get my stuff back " +
                        "I just download Google Drive & my stuff is all there "))
        log.info("result {}", positive)
        assertTrue(positive.labels.isEmpty())

        val positive2 = classifier.test(PredicateContext.create(), TextImpl(
                "obviously worthwhile software. Go and use it"))

        log.info("result {}", positive2)
        assertTrue(positive2.labels.isEmpty())
    }

}