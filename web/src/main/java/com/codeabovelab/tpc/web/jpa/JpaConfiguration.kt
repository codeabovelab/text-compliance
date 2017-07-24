package com.codeabovelab.tpc.web.jpa

import com.codeabovelab.tpc.web.config.JacksonConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

/**
 */
@Import(
        HibernateJpaAutoConfiguration::class
)
@EntityScan(
        basePackageClasses = arrayOf(
                RuleEntity::class
        )
)
@EnableJpaRepositories(
        basePackageClasses = arrayOf(
                RulesRepository::class
        )
)
@EnableTransactionManagement
@Configuration
open class JpaConfiguration {

    @EnableConfigurationProperties(DataSourceProperties::class)
    @Configuration
    open class DataSourceConfig(
        var properties: DataSourceProperties
    ) {

        @Bean
        open fun dataSource() : DataSource? {
            val builder = properties.initializeDataSourceBuilder()
            return builder.build()
        }
    }

    /**
     * Object mapper usable for de/serialize into database
     */
    @Bean(name = arrayOf(BEAN_OBJECT_MAPPER))
    open fun jpaObjectMapper() = createJpaObjectMapper()

    companion object {
        const val BEAN_OBJECT_MAPPER = "jpaObjectMapper"

        fun createJpaObjectMapper() : ObjectMapper {
            val mapper = ObjectMapper()
            mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
            mapper.registerModules(JacksonConfig.commonModules())
            return mapper
        }
    }
}