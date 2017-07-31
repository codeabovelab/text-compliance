package com.codeabovelab.tpc.text

/**
 * Simple implementation of textual.
 */
class TextualImpl(
        override val id: String,
        override val data: String,
        override val parent: Textual?
) : Textual, Text {

    override val length: Int get() = data.length

    override fun read(consumer: TextConsumer) {
        consumer(this, this)
    }
}
