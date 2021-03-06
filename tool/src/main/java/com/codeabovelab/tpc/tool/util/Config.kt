package com.codeabovelab.tpc.tool.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.InputStream
import java.io.OutputStream

/**
 */
object Config {

    const val FILE = "config.yaml"

    val om = ObjectMapper(YAMLFactory())
    val woNulls = ObjectMapper(YAMLFactory())
            .setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))!!


    fun <T: Any> read(dest: T, istream: InputStream) {
        val reader = om.readerForUpdating(dest)
        reader.readValue<T>(istream)
    }

    fun  <T: Any> write(src: T, ostream: OutputStream, withNulls: Boolean = false) {
        if(withNulls) {
            om.writeValue(ostream, src)
        } else {
            woNulls.writeValue(ostream, src)
        }
    }
}