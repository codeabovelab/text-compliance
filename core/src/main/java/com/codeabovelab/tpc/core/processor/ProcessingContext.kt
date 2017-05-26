package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.doc.Document;
import com.codeabovelab.tpc.text.Text;
import com.google.common.collect.ImmutableMap;

/**
 * Processing context, it can be used in concurrent environment.
 * Must be thread save.
 */
class ProcessingContext(val document: Document,
                        val reportBuilder: ProcessorReport.Builder,
                        val rules: List<Rule<*>>) {

    fun onText(text: Text) {
        rules.forEach{
            applyRule(text, it)
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
        return PredicateContext(document, ImmutableMap.copyOf(getAttributes()))
    }

    /**
     * Mutable map of attributes.
     * @return map of attributes
     */
    fun getAttributes(): MutableMap<String, Any> = reportBuilder.attributes
}
