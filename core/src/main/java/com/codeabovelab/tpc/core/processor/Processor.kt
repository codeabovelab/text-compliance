package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.doc.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Must be a thread safe.
 */
class Processor {

    private val rules = ConcurrentHashMap<String, Rule<*>>()

    fun addRule(rule: Rule<*>) {
        val exists = rules.putIfAbsent(rule.id, rule)
        if(exists != null && exists != rule) {
            throw IllegalArgumentException("Already has rule with id: " + rule.id)
        }
    }

    fun process(doc: Document): ProcessorReport {
        val prb = ProcessorReport.Builder()
        prb.documentId = doc.id
        val pc = ProcessingContext(doc, prb, selectRules())
        doc.read(pc::onText)
        return prb.build();
    }

    private fun selectRules(): List<Rule<*>> {
        val rules = ArrayList(this.rules.values)
        rules.sort()
        return Collections.unmodifiableList(rules)
    }
}
