package com.codeabovelab.tpc.core.thread

import com.codeabovelab.tpc.doc.MessageDocument
import java.util.*

/**
 * Present a group of messages together in parent/child relationships based on which messages are replies to which others.
 */
interface MessagesThread {

    object NONE: MessagesThread {
        override val documents: List<MessageDocument>
            get() = Collections.emptyList()
    }

    /**
     * List of messages in thread
     */
    val documents: List<MessageDocument>
}