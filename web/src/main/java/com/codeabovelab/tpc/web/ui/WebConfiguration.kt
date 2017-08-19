package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.web.config.JacksonConfig
import com.codeabovelab.tpc.web.springfox.SpringFoxConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.*
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


/**
 */
@Import(
        JacksonConfig::class,
        HttpMessageConvertersAutoConfiguration::class,
        DispatcherServletAutoConfiguration::class,
        ServletWebServerFactoryAutoConfiguration::class,
        ErrorMvcAutoConfiguration::class,
        HttpEncodingAutoConfiguration::class,
        MultipartAutoConfiguration::class,
        WebMvcAutoConfiguration::class,
        SpringFoxConfiguration::class
)
@ComponentScan(basePackageClasses = arrayOf(WebConfiguration::class))
@Configuration
open class WebConfiguration