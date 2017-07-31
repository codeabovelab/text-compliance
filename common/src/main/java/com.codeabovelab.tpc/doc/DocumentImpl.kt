package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.google.common.collect.ImmutableMap

open class DocumentImpl(b: DocumentImpl.Builder) : AbstractDocument(b), Document {

    open class Builder : AbstractDocument.Builder<Builder>() {
        override fun build(): DocumentImpl {
            return DocumentImpl(this)
        }

    }

    override val attributes: Map<String, Any?> = ImmutableMap.copyOf(b.attributes)

    override fun read(consumer: TextConsumer) {
        consumer(this, body)
        childs.forEach { df -> df.read(consumer) }
    }
}
