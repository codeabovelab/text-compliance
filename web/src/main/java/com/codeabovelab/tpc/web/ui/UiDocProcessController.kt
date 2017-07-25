package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.core.processor.Label
import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.web.docproc.DocProcessor
import com.codeabovelab.tpc.web.docproc.ProcessorReportsStorage
import com.codeabovelab.tpc.web.jpa.ProcessorReportEntity
import com.codeabovelab.tpc.web.jpa.ProcessorReportsRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 */
@RequestMapping( path = arrayOf("/docproc"), produces = arrayOf(MimeTypeUtils.APPLICATION_JSON_VALUE))
@Transactional(propagation = Propagation.REQUIRED)
@RestController
class UiDocProcessController(
        private var reportsRepo: ProcessorReportsRepository,
        private var processorReportsStorage: ProcessorReportsStorage,
        private var processor: DocProcessor
) {

    @RequestMapping("/analyze", method = arrayOf(RequestMethod.POST))
    fun analyze(
            documentId: String,
            @RequestParam(required = false) renew: Boolean
    ): UiProcessorReport? {
        return processor.process(documentId, renew).toUi()
    }

    @RequestMapping("/get", method = arrayOf(RequestMethod.GET))
    fun get(documentId: String): UiProcessorReport? {
        return processorReportsStorage.getLastReportEntity(documentId).toUi()
    }

    @RequestMapping("/getAll", method = arrayOf(RequestMethod.GET))
    fun getAll(documentId: String): List<UiProcessorReport> {
        return reportsRepo.getReportsByDocument(documentId).map { it.toUi()!! }
    }

    @RequestMapping("/delete", method = arrayOf(RequestMethod.POST))
    fun delete(id: Long) {
        return reportsRepo.deleteById(id)
    }

    fun ProcessorReportEntity?.toUi() : UiProcessorReport? {
        if(this == null) {
            return null
        }
        return UiProcessorReport(
                id = id,
                date = date,
                report = processorReportsStorage.reportFromString(data)
        )
    }
}

data class UiProcessorReport(
        val id: Long,
        val date: LocalDateTime,
        val report: ProcessorReport
) {
    val labels: Collection<Label>
        get() = report.labels
}