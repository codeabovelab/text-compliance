package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.codeabovelab.tpc.text.TextImpl
import com.codeabovelab.tpc.util.Asserts
import com.google.common.collect.ImmutableList
import java.time.ZonedDateTime

/**
 */
class MessageDocumentImpl private constructor(builder: Builder): MessageDocument {

    class Builder: MessageDocument.Builder {
        override var id: String? = null
        override var from: String? = null
        override val to: List<String> = ArrayList()
        override var date: ZonedDateTime? = null
        var body: String? = null

        override fun build(): MessageDocument {
            Asserts.nonNullAll(this::id, this::from, this::date)
            return MessageDocumentImpl(this)
        }
    }

    override val from: String = builder.from!!
    override val to: List<String> = ImmutableList.copyOf(builder.to)
    override val date: ZonedDateTime = builder.date!!
    private val body = TextImpl(builder.id!!, builder.body!!)
    override val id: String get() = body.id
    private val fields = DocumentUtils.createFields(this)

    override fun read(consumer: TextConsumer) {
        consumer(this, body)
        fields.forEach { it.read(consumer) }
    }

}