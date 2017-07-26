package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.util.Reflections
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 */
object DocumentUtils {

    fun createField(owner: Document, prop: KProperty<*>): DocumentField.Builder {
        val value = prop.getter.call(owner)?.toString()
        return DocumentFieldImpl.builder()
                .id(prop.name)
                .data(value)
    }

    fun createFields(doc: Document): List<DocumentField> {
        val list = ArrayList<DocumentField>()
        Reflections.forEach(doc) {
            val fieldDesc = this.property.findAnnotation<FieldDesc>()
            if(fieldDesc == null) {
                return@forEach
            }
            val fb = createField(doc, property)
            fb.parent = doc
            list.add(fb.build())
        }
        return list
    }
}