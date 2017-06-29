package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.doc.DocumentImpl
import org.junit.Test

/**
 */
class ProcessorTest {

    @Test
    fun simpleTest() {
        val processor = Processor()
        processor.addRule(Rule("simple", 0F, RegexPredicate("MARK"), SetAttributeAction("found", "mark")))
        val firstReport = processor.process(DocumentImpl.Builder()
          .body("first", "some text MARK \n ant some other MARK text \n MARK")
          .build())
        System.out.println(firstReport)
    }

}