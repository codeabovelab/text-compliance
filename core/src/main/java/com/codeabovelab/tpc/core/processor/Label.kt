package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.util.compare
import com.codeabovelab.tpc.util.thenCompare

/**
 */
data class Label(
    val label: String,
    val similarity: Double
): Comparable<Label> {
    override fun compareTo(other: Label): Int {
        return compare(this.similarity, other.similarity)
                .thenCompare(label, other.label)()
    }
}