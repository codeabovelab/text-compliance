package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer

open class DocumentImpl(b: DocumentImpl.Builder) : AbstractDocument(b), Document {

    open class Builder : AbstractDocument.Builder<Builder>() {
        override fun build(): DocumentImpl {
            return DocumentImpl(this)
        }

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
