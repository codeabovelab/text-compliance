package com.codeabovelab.tpc.tool.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.InputStream
import java.io.OutputStream

/**
 */
object Config {

    val om = ObjectMapper(YAMLFactory())
    val woNulls = ObjectMapper(YAMLFactory())
            .setPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))!!


    fun <T: Any> read(dest: T, istream: InputStream) {
        om.readValue(istream, dest.javaClass)
    }

    fun  <T: Any> write(src: T, ostream: OutputStream, withNulls: Boolean = false) {
        if(withNulls) {
            om.writeValue(ostream, src)
        } else {
            woNulls.writeValue(ostream, src)
        }
    }
}