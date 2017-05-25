package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.text.Text;

/**
 * Test that rule match specified text
 */
interface RulePredicate<out T: PredicateResult> {
    /**
     * Test that specified text in context is match.
     * @param pc context
     * @param text text
     * @return coordinates when match or empty collection otherwise, newer return null
     */
    fun test(pc: PredicateContext, text: Text ): T
}
