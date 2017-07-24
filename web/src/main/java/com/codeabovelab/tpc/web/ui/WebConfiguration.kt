package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.web.config.JacksonConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.context.annotation.Bean



/**
 */
@Import(
        JacksonConfig::class,
        HttpHandlerAutoConfiguration::class,
        ReactiveWebServerAutoConfiguration::class,
        WebFluxAutoConfiguration::class
)
@ComponentScan(basePackageClasses = arrayOf(WebConfiguration::class))
@Configuration
open class WebConfiguration {

    @Configuration
    class WebFluxPreConfiguration {
        //see https://jira.spring.io/browse/SPR-15247
        @Bean
        fun webFluxConfigurer(mapper: ObjectMapper): WebFluxConfigurer {
            return object : WebFluxConfigurer {
                override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer?) {
                    val codecs = configurer!!.defaultCodecs()
                    codecs.jackson2Encoder(Jackson2JsonEncoder(mapper))
                    codecs.jackson2Decoder(Jackson2JsonDecoder(mapper))
                }
            }

        }
    }
}