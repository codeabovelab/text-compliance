package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.codeabovelab.tpc.util.Asserts
import com.google.common.collect.ImmutableList
import java.time.ZonedDateTime

/**
 */
class MessageDocumentImpl private constructor(builder: Builder): AbstractDocument(b = builder), MessageDocument {

    class Builder: AbstractDocument.Builder<Builder>(), MessageDocument.Builder {
        override var from: String? = null
        override val to: MutableList<String> = ArrayList()
        override var date: ZonedDateTime? = null

        override fun build(): MessageDocument {
            Asserts.nonNullAll(this::body, this.body!!::id, this::from, this::date)
            return MessageDocumentImpl(this)
        }
    }

    override val from: String = builder.from!!
    override val to: List<String> = ImmutableList.copyOf(builder.to)
    override val date: ZonedDateTime = builder.date!!
    private val virtualFields = DocumentUtils.createFields(this)

    override fun read(consumer: TextConsumer) {
        fields.forEach { it.read(consumer) }
        virtualFields.forEach { it.read(consumer) }
        consumer(this, body)
    }

}