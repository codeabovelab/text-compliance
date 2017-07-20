package com.codeabovelab.tpc.web.ui

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime

/**
 */
@ControllerAdvice
open class UiErrorHandler(
        @Value("\${spring.application.name:test}")
        private val name: String,
        @Value("\${spring.mvc.log-resolved-exception}")
        private val logResolvedException: Boolean
) {

    @Suppress("LeakingThis")
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * see https://github.com/spring-projects/spring-boot/issues/8625
     */
    @ExceptionHandler
    @ResponseBody
    fun onError(request: ServerHttpRequest, throwable: Throwable): String {
        if(logResolvedException) {
            log.warn("Error at '{}': ", request.path, throwable)
        }
        val date = LocalDateTime.now()
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        pw.print("$name $date\nError:\n")
        throwable.printStackTrace(pw)
        return sw.toString()
    }
}