package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.tool.config.JacksonConfig
import com.codeabovelab.tpc.tool.docs.DocumentConfiguration
import com.codeabovelab.tpc.tool.jpa.JpaConfiguration
import com.codeabovelab.tpc.tool.ui.WebConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

/**
 */
@Import(
        PropertySourcesPlaceholderConfigurer::class,
        ConfigurationPropertiesAutoConfiguration::class,
        JacksonConfig::class,
        WebConfiguration::class,
        JpaConfiguration::class,
        DocumentConfiguration::class
)
@SpringBootConfiguration
open class Application {

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}