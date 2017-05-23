package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.doc.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Must be a thread safe.
 */
public class Processor {

    private final Map<String, Rule> rules = new ConcurrentHashMap<>();

    public void addRule(Rule rule) {
        Rule exists = rules.putIfAbsent(rule.getId(), rule);
        if(exists != null && exists != rule) {
            throw new IllegalArgumentException("Already has rule with id: " + rule.getId());
        }
    }

    public ProcessorReport process(Document doc) {
        ProcessorReport.Builder prb = ProcessorReport.builder();
        prb.setDocumentId(doc.getId());
        ProcessingContext pc = new ProcessingContext(doc, prb, selectRules());
        doc.read(pc::onText);
        return prb.build();
    }

    private List<Rule> selectRules() {
        List<Rule> rules = new ArrayList<>(this.rules.values());
        rules.sort(null);
        return Collections.unmodifiableList(rules);
    }
}
