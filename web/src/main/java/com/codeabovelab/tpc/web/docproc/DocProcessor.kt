package com.codeabovelab.tpc.web.docproc

import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.processor.ProcessModifier
import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.web.docs.DocsStorage
import com.codeabovelab.tpc.web.docs.ThreadResolverService
import com.codeabovelab.tpc.web.jpa.ProcessorReportEntity
import com.codeabovelab.tpc.web.rules.RulesLoader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 */
@Component
class DocProcessor(
        private val docsStorage: DocsStorage,
        private val rulesLoader: RulesLoader,
        private val threadResolver: ThreadResolverService,
        private val reportsStorage: ProcessorReportsStorage
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private fun processorReport(doc: Document): ProcessorReport {
        val processor = Processor(
                threadResolver = threadResolver.backend,
                sentenceIteratorFactory = SentenceIteratorFactoryImpl(UimaFactory.create(morphological = true))
        )
        val rules = rulesLoader.getRules()
        rules.forEach {
            processor.addRule(it)
        }
        val modifier = ProcessModifier()
        val report = processor.process(doc = doc, modifier = modifier)
        return report
    }

    /**
     * Start document processing
     */
    fun process(id: String, renew: Boolean): ProcessorReportEntity {
        var reportEntity = if(renew) {
            null
        } else {
            try {
                reportsStorage.getLastReportEntity(id)
            } catch (e: Exception) {
                log.error("Error while load report: ", e)
                null
            }
        }
        if (reportEntity == null) {
            val doc = docsStorage[id]!!
            val report = processorReport(doc)
            reportEntity = reportsStorage.saveReport(report)
        }
        return reportEntity
    }


}