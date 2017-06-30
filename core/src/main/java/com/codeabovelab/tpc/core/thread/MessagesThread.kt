package com.codeabovelab.tpc.core.thread

import com.codeabovelab.tpc.doc.Document
import java.util.*

/**
 * Present a group of messages together in parent/child relationships based on which messages are replies to which others.
 */
interface MessagesThread {

    object NONE: MessagesThread {
        override val documents: List<String>
            get() = Collections.emptyList()

        override fun forEach(consumer: ThreadConsumer) {
            //none
        }
    }

    /**
     * List of messages in thread
     */
    val documents: List<String>

    /**
     * Iterate over thread messages. Note that it may be time consumption, because need
     * to load messages. Also sometime it may can not load message for specified id, and will
     * pass null document into consumer.
     */
    fun forEach(consumer: ThreadConsumer)
}

typealias ThreadConsumer = (id: String, doc: Document?) -> Unit