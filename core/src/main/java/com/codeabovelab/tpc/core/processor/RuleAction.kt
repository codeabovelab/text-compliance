package com.codeabovelab.tpc.core.processor

import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * Must be a thread safe.
 */
interface RuleAction<in T: PredicateResult<*>> {

    fun apply(context: RuleContext, predicateResult: T)

    /**
     * Action which is do nothing ( No OPeration ).
     */
    @JsonTypeName("NoOpAction")
    object NOP: RuleAction<PredicateResult<*>> {
        override fun apply(context: RuleContext, predicateResult: PredicateResult<*>) {
            //none
        }
    }
}
