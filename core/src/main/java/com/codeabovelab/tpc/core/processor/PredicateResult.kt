package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.TextCoordinates

/**
 */
open class PredicateResult<out E: PredicateResult.Entry>(val entries: List<E>) {

    open class Entry(val coordinates: TextCoordinates)

    fun isEmpty() = entries.isEmpty()
}