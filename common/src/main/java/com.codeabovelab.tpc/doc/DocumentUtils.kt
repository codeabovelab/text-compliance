package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.util.Reflections
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 */
object DocumentUtils {

    fun createField(owner: Document, prop: KProperty<*>): DocumentFieldImpl.Builder {
        val value = prop.getter.call(owner)?.toString()
        return DocumentFieldImpl.Builder()
                .id(prop.name)
                .data(value)
    }

    /**
     * Add marked fields and specified attributes into immutablemap and return it.
     */
    fun addAttributes(doc: Document, map: Map<String, Any?>): Map<String, Any?> {
        // we use map instead of ImmutableMap.Builder because it throw error on adding same key twice
        val out = HashMap<String, Any?>()
        out.putAll(map)
        Reflections.forEach(doc) {
            val fieldDesc = this.property.findAnnotation<FieldDesc>()
            if(fieldDesc == null) {
                return@forEach
            }
            val value = this.propertyValue
            out.put(this.property.name, value)
        }
        return Collections.unmodifiableMap(out)
    }
}