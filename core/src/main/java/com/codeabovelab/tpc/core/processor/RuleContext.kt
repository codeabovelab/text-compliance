package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.Text

/**
 */
class RuleContext(
        val processingContext: ProcessingContext,
        val text: Text
) {

    val textReport = ProcessorReport.TextReport.Builder()

    fun <T: PredicateResult<*>> handleRule(rule: Rule<T>) {
        val predicate = rule.predicate
        val predRes = predicate.test(processingContext.getPredicateContext(), text)
        if(predRes.isEmpty()) {
            return
        }
        textReport.rules.put(rule.id, RuleReport(rule.id, predRes))
        rule.action.apply(this, predRes)
    }
}