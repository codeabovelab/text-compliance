package com.codeabovelab.tpc.text

/**
 * Something that has text.
 */
interface Textual {

    /**
     * The id of textual. May be uuid or something else. Must be unique for siblings of its parent.
     * @return non null string
     */
    val id: String

    /**
     * Reference to parent, may be null
     */
    val parent: Textual?

    /**
     * Sequentially invoke consumer on internal text.
     * @param consumer non null value
     */
    fun read(consumer: TextConsumer)
}

typealias TextConsumer = (Textual, Text) -> Unit