package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.Text

/**
 * Predicate which determine of applicability of rule. <p/>
 * Result also can be [Labeled], labels from result will be appeared in [ProcessorReport].
 */
interface RulePredicate<out T: PredicateResult<*>> {
    /**
     * Predicate which determine of applicability of rule.
     * @param pc context
     * @param text text
     * @return coordinates when match or empty collection otherwise, newer return null
     */
    fun test(pc: PredicateContext, text: Text ): T
}
