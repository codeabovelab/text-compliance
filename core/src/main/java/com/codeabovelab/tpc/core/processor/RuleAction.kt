package com.codeabovelab.tpc.core.processor

/**
 * Must be a thread safe.
 */
interface RuleAction<in T: PredicateResult<*>> {

    companion object {
        /**
         * Action which is do nothing ( No OPeration ).
         */
        val NOP = object: RuleAction<PredicateResult<*>> {
            override fun apply(context: RuleContext, predicateResult: PredicateResult<*>) {
                //none
            }
        }
    }

    fun apply(context: RuleContext, predicateResult: T)
}
