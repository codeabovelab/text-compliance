package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.collect.ImmutableList
import java.util.*

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

    }

    override val id: String
    val body: TextImpl
    val fields: List<DocumentField>

    init {
        this.id = b.id!!
        this.body = TextImpl(this.id, b.body!!)
        val fb = ImmutableList.builder<DocumentField>()
        b.fields.forEach { fb.add(it.build(id)) }
        this.fields = fb.build()
    }

    override fun read(consumer: TextConsumer) {
        fields.forEach { df -> df.read(consumer) }
        consumer(this, body)
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }
}
