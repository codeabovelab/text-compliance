package com.codeabovelab.tpc.web.rules

import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.objuri.ClassScheme
import com.codeabovelab.tpc.objuri.ObjUri
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.tool.process.PredicateProvider
import com.codeabovelab.tpc.web.jpa.RuleEntity
import com.codeabovelab.tpc.web.jpa.RulesRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 */
@Component
class RulesLoader(
        private var repository: RulesRepository,
        @Value("\${texaco.processor.classifier.dir}")
        private val classifierDir: String
) {

    final val predicates: ObjUri<RulePredicate<*>>
    final val actions: ObjUri<RuleAction<*>>

    init {
        //TODO in future we must move LearnConfig & etc to external initializer, possibly it can be lazy
        val lc = LearnConfig()
        val learnedDir = LearnConfig.learnedDir(classifierDir)
        lc.configure(learnedDir.config)
        val pp = PredicateProvider(
                learnedDir = learnedDir,
                learnedConfig = lc
        )
        val preds = ClassScheme.Builder<RulePredicate<*>>()
        pp.publish {
            preds.factories.add(it)
        }
        preds.factories.add(::RegexPredicate)
        preds.factories.add(::ParticipantPredicate)
        predicates = ObjUri(preds.build())

        val acts = ClassScheme.Builder<RuleAction<*>>()
        acts.factories.add(::SetAttributeAction)
        acts.factories.add(this::createApplyRulesAction)
        actions = ObjUri(acts.build())
    }

    private fun createApplyRulesAction(actions: List<String>): ApplyRulesAction {
        val actionInstances = actions.map {
            loadById(it)
        }
        return ApplyRulesAction(actionInstances)
    }

    fun getRules(): List<Rule<*>> {
        val entities = repository.findAll()
        return entities.map { this.loadFromEntity(it) }
    }

    private fun loadById(ruleId: String): Rule<*> {
        val entity = repository.findByRuleId(ruleId)
        return loadFromEntity(entity!!)
    }

    private fun loadFromEntity(entity: RuleEntity): Rule<*> {
        val action = if (entity.action == null) {
            RuleAction.NOP
        } else {
            actions.create(entity.action!!)
        }
        return Rule(
                id = entity.ruleId,
                weight = entity.weight,
                predicate = predicates.create(entity.predicate),
                action = action
        )
    }
}