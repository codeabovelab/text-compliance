package com.codeabovelab.tpc.web.docs

import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.doc.DocumentsRepository
import com.codeabovelab.tpc.web.jpa.DocsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

/**
 */
@Component
class DocsStorage(
        private val repository: DocsRepository,
        private val readers: DocumentReaders,
        private val mapper: ObjectMapper
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

    fun getReport(id: String): ProcessorReport? {
        val entity = repository.findByDocumentId(id)!!
        val report = entity.report
        if(report == null) {
            return null
        }
        return mapper.readValue(report, ProcessorReport::class.java)
    }

    fun saveReport(report: ProcessorReport) {
        val entity = repository.findByDocumentId(report.documentId)!!
        entity.report = mapper.writeValueAsString(report)
        repository.save(entity)
    }
}