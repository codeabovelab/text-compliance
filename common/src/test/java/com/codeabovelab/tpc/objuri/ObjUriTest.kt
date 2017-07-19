package com.codeabovelab.tpc.objuri

import org.junit.Assert.*
import org.junit.Test
import kotlin.reflect.full.primaryConstructor

/**
 */
class ObjUriTest {
    @Test
    fun test() {
        val ou = ObjUri(ClassScheme(factories = SomeObject::class.primaryConstructor!!))

        var obj : SomeObject = ou.create("class:${SomeObject::class.qualifiedName}?intArg=234&longArg=${Long.MAX_VALUE}&strArg=%3Fa%3Db")
        assertEquals(SomeObject(234, Long.MAX_VALUE, "?a=b"), obj)

        obj = ou.create("class:${SomeObject::class.qualifiedName}")
        assertEquals(SomeObject(), obj)
    }
}

data class SomeObject(
        val intArg: Int = 0,
        val longArg: Long = 0L,
        val strArg: String = ""
) {
}