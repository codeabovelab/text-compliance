package com.codeabovelab.tpc.core.processor

/**
 */
class Rule<T: PredicateResult<*>>(
    val id: String,
    val weight: Float,
    val predicate: RulePredicate<T>,
    val action: RuleAction<T> = RuleAction.NOP
): Comparable<Rule<*>> {

    override fun compareTo(other: Rule<*>): Int {
        return this.weight.compareTo(other.weight)
    }
}
