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

        override fun getDocument(id: String) = null
    }

    /**
     * List of messages in thread. In order from first to newest message (include current).
     */
    val documents: List<String>

    /**
     * Gte document from current thread. Can return null even id present in [documents]
     */
    fun getDocument(id: String): Document?

    /**
     * Iterate over thread messages. In same order with [documents]. Note that it may be time consumption, because need
     * to load messages. Also sometime it may can not load message for specified id, and will
     * pass null document into consumer.
     */
    fun forEach(consumer: ThreadConsumer) {
        for(id in documents) {
            val doc = getDocument(id)
            consumer(id, doc)
        }
    }
}

typealias ThreadConsumer = (id: String, doc: Document?) -> Unit