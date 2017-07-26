package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.codeabovelab.tpc.text.TextImpl
import com.codeabovelab.tpc.text.Textual

/**
 */
class DocumentFieldImpl private constructor(b: DocumentFieldImpl.Builder) : DocumentField {

    class Builder : DocumentField.Builder {
        override var id: String? = null
        override var parent: Textual? = null
        var data: String? = null

        fun id(id: String): Builder {
            this.id = id
            return this
        }

        fun data(data: String?): Builder {
            this.data = data
            return this
        }

        override fun build(): DocumentFieldImpl {
            return DocumentFieldImpl(this)
        }
    }

    override val id: String = b.id!!
    override val parent: Textual? = b.parent!!
    val data: TextImpl

    init {
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
