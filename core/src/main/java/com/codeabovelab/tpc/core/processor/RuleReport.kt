package com.codeabovelab.tpc.core.processor

/**
 */
data class RuleReport<out T : PredicateResult<*>>(val ruleId: String, val result: T)
