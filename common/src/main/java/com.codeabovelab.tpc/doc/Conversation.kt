package com.codeabovelab.tpc.doc

/**
 * Created by pronto on 6/3/17.
 */
interface Conversation {
    /**
     * Unique id of chat. May be uuid or something else.
     * @return non null string
     */
    val id: String

    val documents: List<Document>
}