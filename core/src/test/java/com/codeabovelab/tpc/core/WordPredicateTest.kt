package com.codeabovelab.tpc.core

import com.codeabovelab.tpc.core.sw.WordPredicate
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.core.sw.KeywordHashMatcher
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.io.Resources
import org.junit.Test
import org.junit.Ignore
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets

/**
 */
@Ignore
class WordPredicateTest {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    @Test
    fun test() {

        val tc = WordPredicate(
                uima = SentenceIteratorImpl.uimaResource(pos = true, morphological = true),
                wordSupplier = {it.word.str},
                keywordMatcher = KeywordHashMatcher(setOf("hell"))
        )

        val texts = Resources.readLines(Resources.getResource(this.javaClass, "samples.txt"), StandardCharsets.UTF_8)
        var i = 0
        val pc = PredicateContext(document = DocumentImpl.builder().id("test_doc").body("<none>").build(), attributes = emptyMap())
        for(text in texts) {
            println(i++)
            val res = tc.test(pc, TextImpl("sample_" + i, text))
            res.entries.forEach {
                val offset = it.coordinates.offset
                val sentence = text.substring(offset, offset + it.coordinates.length)
                log.info(sentence + ": " + it.word)
            }
        }
    }


}
