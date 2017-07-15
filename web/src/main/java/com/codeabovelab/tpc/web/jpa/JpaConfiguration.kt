package com.codeabovelab.tpc.web.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
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
}