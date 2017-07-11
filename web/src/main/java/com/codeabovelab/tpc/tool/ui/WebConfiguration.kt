package com.codeabovelab.tpc.tool.ui

import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 */
@Import(
        HttpHandlerAutoConfiguration::class,
        ReactiveWebServerAutoConfiguration::class,
        WebFluxAutoConfiguration::class
)
@ComponentScan(basePackageClasses = arrayOf(WebConfiguration::class))
@Configuration
open class WebConfiguration {
}