package com.codeabovelab.tpc.core.processor

/**
 */
class SetAttributeAction(
    val name: String,
    val value: Any
) : RuleAction<PredicateResult<*>> {

    override fun apply(context: RuleContext, predicateResult: PredicateResult<*>) {
        context.processingContext.attributes.put(name, value)
    }
}
