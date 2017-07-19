package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.web.jpa.RuleEntity
import com.codeabovelab.tpc.web.jpa.RulesRepository
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 */
@RequestMapping("/rule")
@RestController
class UiRuleController(
        private var repository: RulesRepository
) {

    @RequestMapping("/list", method = arrayOf(RequestMethod.GET))
    fun list(): List<String> {
        return repository.findAll().map { it.ruleId }
    }

    @RequestMapping("/get", method = arrayOf(RequestMethod.GET))
    fun get(id : String): UiRule? {
        val entity = repository.findByRuleId(id)
        return entity.toUi()
    }

    @RequestMapping("/set", method = arrayOf(RequestMethod.POST))
    fun set(@RequestBody ui: UiRule) {
        val entity = repository.findByRuleId(ui.ruleId) ?: RuleEntity()
        ui.toEntity(entity)
        repository.save(entity)
    }

}

fun RuleEntity?.toUi() : UiRule? {
    if(this == null) {
        return null
    }
    return UiRule(
            ruleId = ruleId,
            action = action,
            predicate = predicate,
            weight = weight
    )
}

class UiRule(
    var ruleId: String,
    var weight: Float,
    var predicate: String,
    var action: String?
) {

    fun toEntity(entity: RuleEntity) : RuleEntity {
        entity.action = action
        entity.weight = weight
        entity.predicate = predicate
        entity.ruleId = ruleId
        return entity
    }
}

