package com.codeabovelab.tpc.doc

/**
 * Container for related documents
 */
interface Conversation {
    /**
     * Unique id of chat. May be uuid or something else.
     * @return non null string
     */
    val id: String

    val documents: List<Document>
}