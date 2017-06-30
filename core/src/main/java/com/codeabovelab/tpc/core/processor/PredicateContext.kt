package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.core.thread.MessagesThread
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentImpl

/**
 * Read only context for predicate evaluation.
 */
data class PredicateContext(
        val document: Document,
        val attributes: Map<String, Any>,
        val thread: MessagesThread
) {
    companion object {
        val STUB = PredicateContext(
                document = DocumentImpl.builder().body("test_doc", "<none>").build(),
                attributes = emptyMap(),
                thread = MessagesThread.NONE
        )
    }
}
