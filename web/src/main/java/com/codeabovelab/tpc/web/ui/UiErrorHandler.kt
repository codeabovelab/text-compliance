package com.codeabovelab.tpc.web.ui

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 */
@ControllerAdvice
open class UiErrorHandler(
        @Value("\${spring.application.name:test}")
        private val name: String
) {

    @Suppress("LeakingThis")
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * see https://github.com/spring-projects/spring-boot/issues/8625
     */
    @ExceptionHandler
    fun onError(request: HttpServletRequest, response: HttpServletResponse, throwable: Throwable): ResponseEntity<*> {
        val acceptText = isAcceptText(request)
        val headers = HttpHeaders()
        val body: Any = if (acceptText) {
            val date = LocalDateTime.now()
            val sw = StringWriter()
            PrintWriter(sw).use {
                it.print("$name $date\nError:\n")
                throwable.printStackTrace(it)
            }
            headers.contentType = MediaType.TEXT_PLAIN
            sw.toString()
        } else {
            val sw = StringWriter()
            PrintWriter(sw).use { throwable.printStackTrace(it) }
            UiError(
                    date = LocalDateTime.now(),
                    stacktrace = sw.toString()
            )
        }
        val status = HttpStatus.valueOf(response.status)
        return ResponseEntity(body, headers, status)
    }

    private fun isAcceptText(request: HttpServletRequest): Boolean {
        return try {
            val accept = MimeTypeUtils.parseMimeTypes(request.getHeader("Accept"))
            accept.any { it.includes(MimeTypeUtils.TEXT_PLAIN) || it.includes(MimeTypeUtils.TEXT_HTML) }
        } catch (s: Exception) {
            // wrong header
            false
        }
    }
}

data class UiError(
        val date: LocalDateTime,
        val stacktrace: String
)