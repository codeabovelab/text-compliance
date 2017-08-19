package com.codeabovelab.tpc.web.config

import com.codeabovelab.tpc.web.docproc.DocProcessor
import com.codeabovelab.tpc.web.docproc.ProcessorReportsStorage
import com.codeabovelab.tpc.web.job.JobsManager
import com.codeabovelab.tpc.web.rules.RulesLoader
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 */
@Import(
        JobsManager::class,
        DocProcessor::class,
        RulesLoader::class,
        ProcessorReportsStorage::class
)
@Configuration
open class BeansConfig