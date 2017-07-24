package com.codeabovelab.tpc.web.docs

import com.codeabovelab.tpc.core.thread.ThreadResolver
import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.doc.DocumentsRepository
import com.codeabovelab.tpc.doc.MessageDocument
import com.codeabovelab.tpc.web.jpa.DocsRepository
import org.springframework.stereotype.Component

/**
 */
@Component
class ThreadResolverService(
        private val repository: DocsRepository,
        private val readers: DocumentReaders,
        private val repo: DocumentsRepository
) {
    val backend = ThreadResolver(repo)

    fun getRelated(documentId: String) : List<String> {
        val doc = repo[documentId]
        if(doc !is MessageDocument) {
            return listOf()
        }
        val thread = backend.getThread(doc)
        return thread.documents
    }
}