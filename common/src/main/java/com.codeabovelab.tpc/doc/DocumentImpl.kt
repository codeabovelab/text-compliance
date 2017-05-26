package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.collect.ImmutableList
import lombok.Data

import java.util.ArrayList

class DocumentImpl(b: DocumentImpl.Builder) : Document {

    class Builder : Document.Builder {
        override var id: String? = null
        var body: String? = null
        val fields = ArrayList<DocumentField.Builder>()

        fun id(id: String): Builder {
            this.id = id
            return this
        }

        fun body(body: String): Builder {
            this.body = body
            return this
        }

        fun addField(field: DocumentField.Builder): Builder {
            this.fields.add(field)
            return this
        }

        override fun build(): DocumentImpl {
            return DocumentImpl(this)
        }

        fun getFields(): List<DocumentField.Builder> {
            return fields
        }
    }

    override val id: String
    val body: TextImpl
    val fields: List<DocumentField>

    init {
        this.id = b.id!!
        this.body = TextImpl(this.id, b.body!!)
        val fb = ImmutableList.builder<DocumentField>()
        b.fields.forEach { fb.add(it.build(b)) }
        this.fields = fb.build()
    }

    override fun read(consumer: TextConsumer) {
        consumer(body)
        fields.forEach { df -> df.read(consumer) }
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }
}
