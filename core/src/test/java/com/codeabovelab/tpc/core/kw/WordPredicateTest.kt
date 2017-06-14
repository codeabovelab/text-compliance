package com.codeabovelab.tpc.core.kw

import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.io.Resources
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

/**
 */
@Ignore
class WordPredicateTest {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    @Test
    fun test() {

        val keyWord : String ="hell"
        val tc = WordPredicate(
                uima = SentenceIteratorImpl.Companion.uimaResource(pos = true, morphological = true),
                keywordMatcher = KeywordHashMatcher(setOf(keyWord))
        )

        val texts = Resources.readLines(Resources.getResource(this.javaClass, "samples.txt"), StandardCharsets.UTF_8)
        var i = 0
        val pc = PredicateContext(document = DocumentImpl.Companion.builder().id("test_doc").body("<none>").build(), attributes = emptyMap())
        for (text in texts) {
            println(i++)
            val res = tc.test(pc, TextImpl("sample_" + i, text))
            res.entries.forEach {
                val offset = it.coordinates.offset
                val sentence = text.substring(offset, offset + it.coordinates.length)
                log.info(sentence + ": " + it.word)
                Assert.assertNotNull(it.word)
                Assert.assertThat(it.word, CoreMatchers.hasItem(WordSearchResult.Label(keyWord, keyWord)))
            }
        }
    }


}
