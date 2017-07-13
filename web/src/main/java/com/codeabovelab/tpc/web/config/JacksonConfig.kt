package com.codeabovelab.tpc.web.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 */
@Configuration
open class JacksonConfig {

    @Bean
    open fun jaksonCustomizer() = Jackson2ObjectMapperBuilderCustomizer {
        it.modules(KotlinModule())
    }
}