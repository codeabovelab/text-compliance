package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.util.Reflections
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 */
object DocumentUtils {

    fun createField(prop: KProperty<*>): DocumentField.Builder {
        val value = prop.getter.call()?.toString()
        return DocumentFieldImpl.builder()
                .name(prop.name)
                .data(value)
    }

    fun createFields(doc: Document): List<DocumentField> {
        val list = ArrayList<DocumentField>()
        Reflections.forEach(doc) {
            val fieldDesc = this.property.findAnnotation<FieldDesc>()
            if(fieldDesc == null) {
                return@forEach
            }
            list.add(createField(property).build(doc.id))
        }
        return list
    }
}