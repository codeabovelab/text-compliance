package com.codeabovelab.tpc.util

/**
 * Set of function for sequence comparison. <p/>
 * Sample:
 * <pre>
 *     return compare(this.one, other.one)
 *              .then(this.two, other.two)
 *              .then(this.three, other.three)
 *              .invoke()
 * </pre>
 * It return first non zero result.
 */

/**
 * Func that always return zero, used for equal parameters
 */
private val RETURN_ZERO = { 0 }

/**
 * Entry point, compare objects and return chain
 */
fun <T : Comparable<T>> compare(left: T, right: T): ComparatorChain {
    val res = left.compareTo(right)
    return if (res == 0) {
        RETURN_ZERO
    } else {
        { res }
    }
}

/**
 * Allow define second comparison, it uses when previous value are equals.
 */
fun <T : Comparable<T>> ComparatorChain.thenCompare(left: T, right: T): ComparatorChain {
    if (this == RETURN_ZERO) {
        return compare(left, right)
    }
    return this
}

typealias ComparatorChain = () -> Int