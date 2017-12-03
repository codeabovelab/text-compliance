package com.codeabovelab.tpc.web.docs

import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.doc.DocumentsRepository
import com.codeabovelab.tpc.web.jpa.DocsRepository
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

/**
 */
@Component
class DocsStorage(
        private val repository: DocsRepository,
        private val readers: DocumentReaders
): DocumentsRepository {

    override fun get(id: String): Document? {
        val docEntity = repository.findByDocumentId(id) ?: return null
        val reader = readers[docEntity.type]!!
        val istr = ByteArrayInputStream(docEntity.data)
        val builder = istr.use {
            reader.read(docEntity.documentId, it)
        }
        return builder.build()
    }
}