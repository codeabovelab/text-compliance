package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.core.thread.MessagesThread
import com.codeabovelab.tpc.core.thread.ThreadResolver
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.MessageDocument

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * Must be a thread safe.
 */
class Processor(
        private val threadResolver: ThreadResolver? = null
) {

    private val rules = ConcurrentHashMap<String, Rule<*>>()

    fun addRule(rule: Rule<*>) {
        val exists = rules.putIfAbsent(rule.id, rule)
        if(exists != null && exists != rule) {
            throw IllegalArgumentException("Already has rule with id: " + rule.id)
        }
    }

    fun process(doc: Document, modifier: ProcessModifier = ProcessModifier.DEFAULT): ProcessorReport {
        val thread = detectThread(doc)
        val pc = ProcessingContext(document = doc,
                modifier = modifier,
                rules = selectRules(),
                thread = thread)
        doc.read(pc::onText)
        return pc.build()
    }

    private fun detectThread(doc: Document): MessagesThread {
        return if(threadResolver != null && doc is MessageDocument) {
            threadResolver.getThread(doc)
        } else {
            MessagesThread.NONE
        }
    }

    private fun selectRules(): List<Rule<*>> {
        val rules = ArrayList(this.rules.values)
        rules.sort()
        return Collections.unmodifiableList(rules)
    }
}
