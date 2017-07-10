package com.codeabovelab.tpc.tool.ui

import org.springframework.beans.factory.annotation.Value
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
        private val name: String
) {

    /**
     * see https://github.com/spring-projects/spring-boot/issues/8625
     */
    @ExceptionHandler
    @ResponseBody
    fun onError(throwable: Throwable): String {
        val date = LocalDateTime.now()
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        pw.print("$name $date\nError:\n")
        throwable.printStackTrace(pw)
        return sw.toString()
    }
}