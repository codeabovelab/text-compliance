package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.text.Text;

/**
 * Must be a thread safe.
 */
interface RuleAction<in T: PredicateResult<*>> {
    fun apply(pc: ProcessingContext, text: Text, predicateResult: T)
}
