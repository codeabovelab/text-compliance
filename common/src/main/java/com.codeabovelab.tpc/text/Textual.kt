package com.codeabovelab.tpc.text

/**
 * Something that has text.
 */
interface Textual {

    /**
     * Unique id of textual. May be uuid or something else.
     * @return non null string
     */
    val id: String

    /**
     * Sequentially invoke consumer on internal text.
     * @param consumer non null value
     */
    fun read(consumer: TextConsumer)
}

typealias TextConsumer = (Textual, Text) -> Unit