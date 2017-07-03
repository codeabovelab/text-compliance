package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.TextCoordinates
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 */
open class PredicateResult<out E: PredicateResult.Entry>(val entries: List<E>) {

    open class Entry(val coordinates: TextCoordinates)

    /**
     * This method provide result of [RulePredicate] triggering.
     * Default implementation delegate call to [entries].isEmpty()
     */
    @JsonIgnore
    open fun isEmpty() = entries.isEmpty()
}