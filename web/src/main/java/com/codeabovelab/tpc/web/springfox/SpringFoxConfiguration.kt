package com.codeabovelab.tpc.web.springfox

import com.google.common.base.Predicates
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 */
@EnableSwagger2
class SpringFoxConfiguration {

    @Bean
    fun swaggerDocket(): Docket {
        val doc = Docket(DocumentationType.SWAGGER_2)
        doc.select()
            .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
            .paths(PathSelectors.any())
            .build()
        doc.pathMapping("/")
        return doc
    }

}