package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.core.thread.MessagesThread
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.Textual
import com.google.common.collect.ImmutableMap

/**
 * Processing context, it can be used in concurrent environment.
 * Must be thread save.
 */
class ProcessingContext(
        val document: Document,
        val modifier: ProcessModifier,
        val reportBuilder: ProcessorReport.Builder,
        val rules: List<Rule<*>>,
        val thread: MessagesThread
) {

    fun onText(textual: Textual, text: Text) {
        if(!modifier.filter(textual)) {
            return
        }
        val handledText = modifier.textHandler(text)
        rules.forEach{
            applyRule(handledText, it)
        }
    }

    private fun <T: PredicateResult<*>> applyRule(text: Text, rule: Rule<T>) {
        val predicate = rule.predicate
        val predRes = predicate.test(getPredicateContext(), text)
        if(predRes.isEmpty()) {
           return
        }
        reportBuilder.rules.add(RuleReport(rule.id, predRes))
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
