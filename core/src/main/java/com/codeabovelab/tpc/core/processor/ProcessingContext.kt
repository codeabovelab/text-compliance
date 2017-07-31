package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.core.thread.MessagesThread
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.Textual
import com.google.common.collect.ImmutableMap
import java.util.*

/**
 * Processing context, it can be used in concurrent environment.
 * Must be thread save.
 */
class ProcessingContext(
        val document: Document,
        val modifier: ProcessModifier,
        val rules: List<Rule<*>>,
        val thread: MessagesThread
) {

    val reportBuilder = ProcessorReport.Builder()
    private val textualBuilders = IdentityHashMap<Textual, ProcessorReport.TextReport.Builder>()

    fun build(): ProcessorReport {
        reportBuilder.documentId = document.id
        fun buildTextReport(textual: Textual): ProcessorReport.TextReport? {
            val builder = textualBuilders[textual]
            if(builder == null) {
                return null
            }
            for(child in textual.childs) {
                val report = buildTextReport(child)
                if(report != null) {
                    builder.childs[child.id] = report
                }
            }
            return builder.build()
        }
        reportBuilder.report = buildTextReport(document)!!
        return reportBuilder.build()
    }

    fun onText(textual: Textual, text: Text) {
        if(!modifier.filter(textual)) {
            return
        }
        val handledText = modifier.textHandler(text)
        val trb = ProcessorReport.TextReport.Builder()
        trb.textId = textual.id
        rules.forEach{
            applyRule(handledText, it, trb)
        }
        textualBuilders.put(textual, trb)
    }

    private fun <T: PredicateResult<*>> applyRule(text: Text, rule: Rule<T>, builder: ProcessorReport.TextReport.Builder) {
        val predicate = rule.predicate
        val predRes = predicate.test(getPredicateContext(), text)
        if(predRes.isEmpty()) {
           return
        }
        builder.rules.put(rule.id, RuleReport(rule.id, predRes))
        rule.action.apply(this, text, predRes)
    }

    private fun getPredicateContext(): PredicateContext {
        // now predicate context has snapshot of attributes
        // therefore we can not share it instance
        return PredicateContext(
                document = document,
                attributes = ImmutableMap.copyOf(getAttributes()),
                thread = thread
        )
    }

    /**
     * Mutable map of attributes.
     * @return map of attributes
     */
    fun getAttributes(): MutableMap<String, Any> = reportBuilder.attributes
}
