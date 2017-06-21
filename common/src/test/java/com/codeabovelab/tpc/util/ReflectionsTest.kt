package com.codeabovelab.tpc.util

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 */
class ReflectionsTest {

    class TestObject(
        val str: String,
        val other: TestObject? = null
    ) {
        val same = this
        val enum = SomeValues.ONE
    }

    enum class SomeValues {
        ONE, TWO, THREE
    }

    @Test
    fun testForEach() {
        val expect = ArrayDeque(listOf("enum", "other", "str", "same", "str"))
        val to = TestObject("one", TestObject("child"))
        Reflections.forEach(to) {
            println("${this.property.name} = ${this.propertyValue}")
            assertEquals(expect.removeFirst(), this.property.name)
            true
        }
        assertTrue(expect.isEmpty())
    }
}