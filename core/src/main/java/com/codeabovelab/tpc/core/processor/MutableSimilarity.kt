package com.codeabovelab.tpc.core.processor

/**
 */
class MutableSimilarity {
    var value: Double = 0.0
        private set

    operator fun plusAssign(another: Double) {
        val limited = limit(another)
        value = limit(value + limited)
    }

    private fun limit(arg: Double) =
            if (arg > 1.0) {
                1.0
            } else if (arg < -1.0) {
                -1.0
            } else {
                arg
            }
}