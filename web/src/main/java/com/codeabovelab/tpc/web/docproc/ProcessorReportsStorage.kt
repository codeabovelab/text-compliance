package com.codeabovelab.tpc.web.docproc

import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.web.jpa.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 */
@Component
class ProcessorReportsStorage(
        private val docRepository: DocsRepository,
        private val repository: ProcessorReportsRepository,
        @Qualifier(JpaConfiguration.BEAN_OBJECT_MAPPER)
        private val mapper: ObjectMapper
) {

    fun getLastReportEntity(id: String): ProcessorReportEntity? {
        // due to limitations of JPQL we can not simply select only first row
        val list = repository.getReportsByDocument(id)
        return list.getOrNull(0)
    }

    fun saveReport(report: ProcessorReport): ProcessorReportEntity {
        val doc = docRepository.findByDocumentId(report.documentId)!!
        val entity = ProcessorReportEntity()
        entity.date = LocalDateTime.now()
        entity.document = doc
        entity.data = reportToString(report)
        return repository.save(entity)
    }

    fun reportFromString(entity: String): ProcessorReport {
        return mapper.readValue(entity, ProcessorReport::class.java)!!
    }

    private fun reportToString(report: ProcessorReport): String {
        return mapper.writeValueAsString(report)
    }
}