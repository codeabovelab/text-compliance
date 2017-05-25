package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.TextCoordinates

/**
 */
open class PredicateResult(val entries: List<Entry>) {

    open class Entry(val coordinates: TextCoordinates)

    fun isEmpty() = entries.isEmpty()
}