package com.codeabovelab.tpc.web.config

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 */
@Import(JacksonAutoConfiguration::class)
@Configuration
open class JacksonConfig {

    companion object {
        fun commonModules(): List<Module> {
            return arrayListOf(KotlinModule(), JavaTimeModule(), Jdk8Module(), GuavaModule())
        }
    }

    @Configuration
    class JacksonBuilderConfig {

        @Bean
        open fun jaksonCustomizer() = Jackson2ObjectMapperBuilderCustomizer {
            it.modules(commonModules())
            it.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}