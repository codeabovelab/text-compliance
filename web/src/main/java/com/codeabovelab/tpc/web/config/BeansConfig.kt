package com.codeabovelab.tpc.web.config

import com.codeabovelab.tpc.web.docproc.DocProcessor
import com.codeabovelab.tpc.web.job.JobsManager
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 */
@Import(
        JobsManager::class,
        DocProcessor::class
)
@Configuration
open class BeansConfig {
}