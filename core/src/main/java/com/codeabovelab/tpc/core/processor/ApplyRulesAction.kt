package com.codeabovelab.tpc.core.processor

import com.fasterxml.jackson.annotation.JsonTypeName

/**
 */
@JsonTypeName("ApplyRulesAction")
class ApplyRulesAction(
        private val rules: List<Rule<*>>
) : RuleAction<PredicateResult<*>> {

    /**
     * Note that name of below property is must be same with name of factory argument.
     */
    val rulesNames: List<String> get() = rules.map { it.id }

    override fun apply(context: RuleContext, predicateResult: PredicateResult<*>) {
        rules.forEach {
            context.handleRule(it)
        }
    }
}
