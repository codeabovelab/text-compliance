package com.codeabovelab.tpc.tool.jpa

import com.codeabovelab.tpc.tool.rules.RuleEntity
import com.codeabovelab.tpc.tool.rules.RulesRepository
import org.hsqldb.jdbc.JDBCPool
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

/**
 */
@Import(
        JpaConfiguration.PreDeps::class
)
@Configuration
open class JpaConfiguration {

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
    open class PreDeps {

        @Bean
        open fun dataSource(): DataSource {
            val pool = JDBCPool()
            pool.setURL("jdbc:hsqldb:mem:tmp")
            return pool
        }
    }
}