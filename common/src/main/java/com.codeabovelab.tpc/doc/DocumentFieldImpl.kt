package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.codeabovelab.tpc.text.TextImpl

/**
 */
class DocumentFieldImpl(parentId: String, b: DocumentFieldImpl.Builder) : DocumentField {

    class Builder : DocumentField.Builder {
        var name: String? = null
        var data: String? = null

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun data(data: String?): Builder {
            this.data = data
            return this
        }

        override fun build(document: Document.Builder): DocumentFieldImpl {
            return DocumentFieldImpl(document.id!!, this)
        }
    }

    override val id: String
    override val name: String
    val data: TextImpl

    init {
        this.name = b.name!!
        this.id = parentId + this.name
        this.data = TextImpl(this.id, b.data.orEmpty())
    }

    override fun read(consumer: TextConsumer) {
        consumer(this, data)
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }
}
