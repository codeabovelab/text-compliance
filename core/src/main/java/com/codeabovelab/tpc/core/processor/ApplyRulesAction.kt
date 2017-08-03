package com.codeabovelab.tpc.core.processor

/**
 */
class ApplyRulesAction(
        val rules: List<Rule<*>>
) : RuleAction<PredicateResult<*>> {
    override fun apply(context: RuleContext, predicateResult: PredicateResult<*>) {
        rules.forEach {
            context.handleRule(it)
        }
    }
}
