package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.collect.ImmutableList
import java.util.ArrayList

/**
 */
abstract class AbstractDocument(b: AbstractDocument.Builder<*>) : Document {

    abstract class Builder<B: AbstractDocument.Builder<B>>: Document.Builder {
        override var body: Text? = null
        val fields = ArrayList<DocumentField.Builder>()

        @Suppress("UNCHECKED_CAST")
        protected val thiz: B get() = this as B

        fun body(id: String, body: String): B {
            this.body = TextImpl(id, body)
            return thiz
        }

        fun addField(field: DocumentField.Builder): B {
            this.fields.add(field)
            return thiz
        }

        override abstract fun build(): Document

    }

    override val id: String get() = body.id
    val body: Text = b.body!!
    val fields: List<DocumentField>

    init {
        val fb = ImmutableList.builder<DocumentField>()
        b.fields.forEach { fb.add(it.build(id)) }
        this.fields = fb.build()
    }
}