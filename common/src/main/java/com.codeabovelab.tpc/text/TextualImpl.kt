package com.codeabovelab.tpc.text

/**
 */
class TextualImpl(override val id: String, text: String) : Textual {
    private val text: TextImpl

    init {
        this.text = TextImpl(this.id, text)
    }

    override fun read(consumer: TextConsumer) {
        consumer.consume(text)
    }
}
