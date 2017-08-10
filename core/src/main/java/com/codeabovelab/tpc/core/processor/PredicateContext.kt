package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.core.nn.nlp.SentenceIterator
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorFactory
import com.codeabovelab.tpc.core.thread.MessagesThread
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.text.Text

/**
 * Read only context for predicate evaluation.
 */
data class PredicateContext(
        val document: Document,
        val attributes: Map<String, Any>,
        val thread: MessagesThread,
        private val sentenceIteratorFactory: SentenceIteratorFactory
) {
    companion object {
        val STUB = create()

        fun create(
                sentenceIteratorFactory : SentenceIteratorFactory = SentenceIteratorFactory.STUB
        ): PredicateContext {
            return PredicateContext(
                    document = DocumentImpl.Builder().id("test_doc").body("<none>").build(),
                    attributes = emptyMap(),
                    thread = MessagesThread.NONE,
                    sentenceIteratorFactory = sentenceIteratorFactory
            )
        }
    }

    fun sentenceIterator(text: Text) : SentenceIterator {
        return sentenceIteratorFactory.create(text)
    }
}
