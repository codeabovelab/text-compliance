package com.codeabovelab.tpc.web.docproc

import com.codeabovelab.tpc.core.processor.ProcessModifier
import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.core.thread.ThreadResolver
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.web.docs.DocsStorage
import com.codeabovelab.tpc.web.rules.RulesLoader
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 */
@Component
class DocProcessor(
        private val repo: DocsStorage,
        private val rulesLoader: RulesLoader
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val threadResolver = ThreadResolver(repo)

    init {
    }

    private val cache = CacheBuilder.newBuilder().build(object : CacheLoader<String, ProcessorReport>() {
        override fun load(id: String): ProcessorReport {
            var report = try {
                repo.getReport(id)
            } catch (e : Exception) {
                log.error("Error while load report: ", e)
                null
            }
            if (report == null) {
                val doc = repo[id]!!
                report = processorReport(doc)
                repo.saveReport(report)
            }
            return report
        }
    })

    private fun processorReport(doc: Document): ProcessorReport {
        val processor = Processor(threadResolver = threadResolver)
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
    fun process(id: String): Mono<ProcessorReport> {
        return Mono.fromCallable {
            cache.get(id)
        }
    }
}