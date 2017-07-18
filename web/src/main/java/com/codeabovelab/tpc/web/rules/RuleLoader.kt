package com.codeabovelab.tpc.web.rules

import com.codeabovelab.tpc.core.processor.PredicateResult
import com.codeabovelab.tpc.core.processor.Rule
import com.codeabovelab.tpc.core.processor.RuleAction
import com.codeabovelab.tpc.core.processor.RulePredicate
import com.codeabovelab.tpc.objuri.ObjUri
import com.codeabovelab.tpc.web.jpa.RuleEntity
import com.codeabovelab.tpc.web.jpa.RulesRepository
import org.springframework.stereotype.Component

/**
 */
@Component
class RuleLoader(
        private var repository: RulesRepository
) {

    private val predicateObjs = ObjUri<RulePredicate<*>>()
    private val actionObjs = ObjUri<RuleAction<*>>()

    fun getRules(): List<Rule<*>> {
        val entities = repository.findAll()
        return entities.map { this.load<PredicateResult<*>>(it) }
    }

    private fun <T : PredicateResult<*>> load(entity: RuleEntity): Rule<T> {
        val action = if (entity.action == null) {
            RuleAction.NOP
        } else {
            actionObjs.create(entity.action!!)
        }
        return Rule(
                id = entity.ruleId,
                weight = entity.weight,
                predicate = predicateObjs.create(entity.predicate),
                action = action
        )
    }
}