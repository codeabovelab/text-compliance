package com.codeabovelab.tpc.web.rules

import com.codeabovelab.tpc.web.jpa.RuleEntity
import com.codeabovelab.tpc.web.jpa.RulesRepository
import com.codeabovelab.tpc.web.ui.UiRule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.InputStream

/**
 * Load default rules from file and place it to database
 */
@Component
class DefaultRulesInjector(
        private var repository: RulesRepository,
        private var loader: ResourceLoader
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onApplicationEvent(event: ApplicationReadyEvent?) {
        try {
            if(repository.count() == 0L) {
                deploy()
            }
        } catch (e: Exception) {
            log.error("Can not deploy default rules.", e)
        }
    }

    private fun deploy() {
        val res = loader.getResource("classpath:config/rules-default.yaml")
        if(!res.exists()) {
            return
        }
        val rules = load(res.inputStream)
        val entitites = rules.list.map {
            it.toEntity(RuleEntity())
        }
        repository.saveAll(entitites)
    }

    fun load(src: InputStream): Rules {
        val om = ObjectMapper(YAMLFactory()).registerModules(KotlinModule())
        val rules = src.use { om.readValue(src, Rules::class.java) }
        return rules
    }

    data class Rules(
        var list: List<UiRule>
    )
}
