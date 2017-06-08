package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.Text

/**
 * Must be a thread safe.
 */
interface RuleAction<in T: PredicateResult<*>> {

    companion object {
        /**
         * Action which is do nothing ( No OPeration ).
         */
        val NOP = object: RuleAction<PredicateResult<*>> {
            override fun apply(pc: ProcessingContext, text: Text, predicateResult: PredicateResult<*>) {
                //none
            }
        }
    }

    fun apply(pc: ProcessingContext, text: Text, predicateResult: T)
}
