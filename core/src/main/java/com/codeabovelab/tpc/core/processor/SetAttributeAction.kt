package com.codeabovelab.tpc.core.processor

import com.fasterxml.jackson.annotation.JsonTypeName

/**
 */
@JsonTypeName("SetAttributeAction")
class SetAttributeAction(
    val name: String,
    val value: Any
) : RuleAction<PredicateResult<*>> {

    override fun apply(context: RuleContext, predicateResult: PredicateResult<*>) {
        context.processingContext.attributes.put(name, value)
    }
}
