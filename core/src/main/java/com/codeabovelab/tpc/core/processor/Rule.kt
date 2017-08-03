package com.codeabovelab.tpc.core.processor

/**
 */
class Rule<T: PredicateResult<*>>(
    /**
     * Id of rule. It must be unique for whole system.
     */
    val id: String,
    /**
     * Used for order of rules before applying to document. Less weight rules apply first.
     */
    val weight: Float,
    /**
     * Predicate which determine of applicability of rule.
     */
    val predicate: RulePredicate<T>,
    val action: RuleAction<T> = RuleAction.NOP
): Comparable<Rule<*>> {

    override fun compareTo(other: Rule<*>): Int {
        return this.weight.compareTo(other.weight)
    }
}
