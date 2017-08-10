package com.codeabovelab.tpc.core.kw

import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.io.Resources
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

/**
 */
class WordPredicateTest {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    @Test
    fun test() {

        val keyWord : String ="hell"
        val tc = WordPredicate(
                keywordMatcher = KeywordSetMatcher.Builder().add(keyWord, setOf(keyWord)).build()
        )

        val texts = Resources.readLines(Resources.getResource(this.javaClass, "samples.txt"), StandardCharsets.UTF_8)
        var i = 0
        val pc = PredicateContext.create(
                sentenceIteratorFactory = SentenceIteratorFactoryImpl(UimaFactory.create(morphological = true))
        )
        for (text in texts) {
            println(i++)
            val res = tc.test(pc, TextImpl(text))
            res.entries.forEach {
                val offset = it.coordinates.offset
                val sentence = text.substring(offset, offset + it.coordinates.length)
                log.info(sentence + ": " + it.keywords)
                Assert.assertNotNull(it.keywords)
                Assert.assertThat(it.keywords, CoreMatchers.hasItem(keyWord))
            }
        }
    }


}
