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
        override val references: MutableList<Ref> = ArrayList()

        override fun build(): MessageDocument {
            Asserts.notNullAll(this::body, this.body!!::id, this::from, this::date)
            return MessageDocumentImpl(this)
        }
    }

    @FieldDesc
    override val from: String = builder.from!!
    @FieldDesc
    override val to: List<String> = ImmutableList.copyOf(builder.to)
    @FieldDesc
    override val date: ZonedDateTime = builder.date!!
    @FieldDesc
    override val references: List<Ref> = ImmutableList.copyOf(builder.references)
    private val virtualFields = DocumentUtils.createFields(this)

    override fun read(consumer: TextConsumer) {
        fields.forEach { it.read(consumer) }
        virtualFields.forEach { it.read(consumer) }
        consumer(this, body)
    }

}