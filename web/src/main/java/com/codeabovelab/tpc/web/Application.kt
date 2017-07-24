package com.codeabovelab.tpc.web

import com.codeabovelab.tpc.web.config.BeansConfig
import com.codeabovelab.tpc.web.config.JacksonConfig
import com.codeabovelab.tpc.web.docs.DocumentConfiguration
import com.codeabovelab.tpc.web.jpa.JpaConfiguration
import com.codeabovelab.tpc.web.ui.WebConfiguration
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
        //JacksonConfig::class,
        BeansConfig::class,
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