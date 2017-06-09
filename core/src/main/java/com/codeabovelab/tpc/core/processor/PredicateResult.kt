package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.TextCoordinates
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 */
open class PredicateResult<out E: PredicateResult.Entry>(val entries: List<E>) {

    open class Entry(val coordinates: TextCoordinates)

    @JsonIgnore
    fun isEmpty() = entries.isEmpty()
}