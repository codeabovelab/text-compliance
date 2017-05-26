package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.doc.DocumentImpl;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class ProcessorTest {

    @Test
    public void simpleTest() {
        Processor processor = new Processor();
        processor.addRule(new Rule("simple", 0F, new RegexPredicate("MARK"), new SetAttributeAction("found", "mark")));
        ProcessorReport firstReport = processor.process(new DocumentImpl.Builder()
          .id("first")
          .body("some text MARK \n ant some other MARK text \n MARK")
          .build());
        System.out.println(firstReport);
    }

}