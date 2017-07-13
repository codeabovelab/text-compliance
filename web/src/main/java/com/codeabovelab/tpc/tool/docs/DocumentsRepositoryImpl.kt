package com.codeabovelab.tpc.tool.docs

import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.doc.DocumentsRepository
import com.codeabovelab.tpc.tool.jpa.DocsRepository
import java.io.ByteArrayInputStream

/**
 */
class DocumentsRepositoryImpl(
        private val repository: DocsRepository,
        private val readers: DocumentReaders
): DocumentsRepository {


    override fun get(id: String): Document? {
        val docEntity = repository.findByDocumentId(id)
        if(docEntity == null) {
            return null
        }
        val reader = readers[docEntity.type]!!
        val istr = ByteArrayInputStream(docEntity.data)
        val builder = istr.use {
            reader.read(docEntity.documentId, it)
        }
        return builder.build()
    }
}