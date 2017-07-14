package com.codeabovelab.tpc.web.docproc

import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.core.thread.ThreadResolver
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.tool.process.ProcessorConfigurer
import com.codeabovelab.tpc.web.docs.DocsStorage
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 */
@Component
class DocProcessor(
        private val repo: DocsStorage,
        @Value("\${texaco.processor.classifier.dir}")
        private val classifierDir: String
) {
    private val processor = Processor(threadResolver = ThreadResolver(repo))

    init {

        val lc = LearnConfig()
        val learnedDir = LearnConfig.learnedDir(classifierDir)
        lc.configure(learnedDir.config)
        ProcessorConfigurer(
                proc = processor,
                learnedDir = learnedDir,
                learnedConfig = lc
        ).configure()
    }

    private val cache = CacheBuilder.newBuilder().build(object : CacheLoader<String, ProcessorReport>() {
        override fun load(id: String): ProcessorReport {
            var report = repo.getReport(id)
            if (report == null) {
                val doc = repo[id]!!
                report = processor.process(doc = doc)
                repo.saveReport(report)
            }
            return report
        }
    })

    /**
     * Start document processing
     */
    fun process(id: String): Mono<ProcessorReport> {
        return Mono.fromCallable {
            cache.get(id)
        }
    }
}