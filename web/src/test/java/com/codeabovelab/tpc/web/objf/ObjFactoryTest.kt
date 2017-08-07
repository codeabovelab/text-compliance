package com.codeabovelab.tpc.web.objf

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert.*
import org.junit.Test
import kotlin.reflect.full.primaryConstructor

/**
 */
class ObjFactoryTest {
    @Test
    fun test() {
        val ou = ObjFactory.Builder(SomeObject::class)
                .apply {
                    objectMapper = ObjectMapper()
                    factories.add(SomeObject::class.primaryConstructor!!)
                }
                .build()

        val src = """{"@type":"$TYPE","intArg":234,"listArg":["one","two"],"strArg":"?a=b"}"""
        var obj : SomeObject = ou.read(src)
        assertEquals(SomeObject(234, listOf("one", "two"), "?a=b"), obj)
        assertEquals(src, ou.write(obj))
        obj = ou.read("""{"@type":"$TYPE"}""")
        assertEquals(SomeObject(), obj)
    }
}

const val TYPE = "some_object"

@JsonTypeName(TYPE)
data class SomeObject(
        val intArg: Int = 0,
        val listArg: List<String> = listOf(),
        val strArg: String = ""
) {
}