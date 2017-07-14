package com.codeabovelab.tpc.web.job

import com.codeabovelab.tpc.util.ExecutorUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 */
@Component
class JobsManager {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val executor = ExecutorUtils.executorBuilder().name(this.javaClass.simpleName).build()

    fun execute(task: Runnable) {
        log.debug("Pass $task to executor")
        executor.execute {
            task.run()
        }
    }
}