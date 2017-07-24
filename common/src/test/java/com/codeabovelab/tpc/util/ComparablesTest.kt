package com.codeabovelab.tpc.util

import org.junit.Test
import kotlin.test.assertEquals

/**
 */
class ComparablesTest {
    private data class Sample(
            val one: Int,
            val two: Int,
            val three: Int
    ) : Comparable<Sample> {
        override fun compareTo(other: Sample): Int {
            return compare(one, other.one)
                    .thenCompare(two, other.two)
                    .thenCompare(three, other.three)
                    .invoke()
        }
    }

    private fun testEq(expect: Int, left: Sample, right: Sample) {
        println("Test: $left & $right")
        assertEquals(expect, left.compareTo(right))
        assertEquals(-expect, right.compareTo(left))
    }

    @Test
    fun testFuncs() {
        testEq(0, Sample(1, 2,3), Sample(1, 2,3))
        testEq(1, Sample(1, 2,4), Sample(1, 2,3))
        testEq(-1, Sample(1, 2,2), Sample(1, 2,3))
        testEq(1, Sample(2, 2,3), Sample(1, 4,8))

        val src = mutableListOf(
                Sample(1, 2,3),
                Sample(1, 2,4),
                Sample(2, 2,3),
                Sample(1, 1,3),
                Sample(1, 2,2),
                Sample(1, 2,3),
                Sample(1, 4,8)
        )
        src.sort()
        println(src)
    }
}