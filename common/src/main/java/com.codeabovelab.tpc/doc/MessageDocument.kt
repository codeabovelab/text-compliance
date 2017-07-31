package com.codeabovelab.tpc.doc

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

/**
 * Message as document. <p/>
 * Key difference from simple document - defined fields.
 */
interface MessageDocument: Document {

    interface Builder<B: Builder<B>>: Document.Builder<B> {
        /**
         * String which is identified sender (user) of message
         */
        var from: String?
        /**
         * String which is identified destination (user, topic & etc) of message
         */
        val to: List<String>
        /**
         * date of message creation
         */
        var date: ZonedDateTime?
        /**
         * List of references to other documents.
         */
        val references: List<Ref>
        override fun build(): MessageDocument
    }

    /**
     * List of references to other documents.
     */
    @get:JsonIgnore
    val references: List<Ref>
    /**
     * String which is identified sender (user) of message
     */
    @get:JsonIgnore
    val from: String
    /**
     * String which is identified destination (user, topic & etc) of message
     */
    @get:JsonIgnore
    val to: List<String>
    /**
     * date of message creation
     */
    @get:JsonIgnore
    val date: ZonedDateTime
}