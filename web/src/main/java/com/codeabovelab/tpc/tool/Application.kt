package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.tool.ui.UiConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

/**
 */
@Import(
        PropertySourcesPlaceholderConfigurer::class,
        ConfigurationPropertiesAutoConfiguration::class,
        HttpHandlerAutoConfiguration::class,
        ReactiveWebServerAutoConfiguration::class,
        WebFluxAutoConfiguration::class,
        UiConfiguration::class
)
@SpringBootConfiguration
open class Application {

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}