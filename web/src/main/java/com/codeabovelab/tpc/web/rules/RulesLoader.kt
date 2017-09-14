package com.codeabovelab.tpc.web.rules

import com.codeabovelab.tpc.core.namedentity.ThirdPartyNamesPredicate
import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.tool.process.PredicateProvider
import com.codeabovelab.tpc.web.jpa.RuleEntity
import com.codeabovelab.tpc.web.jpa.RulesRepository
import com.codeabovelab.tpc.web.objf.ObjFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 */
@Component
class RulesLoader(
        private var repository: RulesRepository,
        /**
         * We must inject web kind of object mapper, because json is come raw from web.
         */
        private val objectMapper: ObjectMapper,
        @Value("\${texaco.processor.classifier.dir}")
        private val classifierDir: String
) {

    final val predicates: ObjFactory<RulePredicate<*>>
    final val actions: ObjFactory<RuleAction<*>>

    init {
        //TODO in future we must move LearnConfig & etc to external initializer, possibly it can be lazy
        val lc = LearnConfig()
        val learnedDir = LearnConfig.learnedDir(classifierDir)
        lc.configure(learnedDir.config)
        val pp = PredicateProvider(
                learnedDir = learnedDir,
                learnedConfig = lc
        )
        val preds = ObjFactory.Builder(RulePredicate::class)
        preds.objectMapper = objectMapper
        pp.publish {
            preds.factories.add(it)
        }
        preds.factories.add(::RegexPredicate)
        preds.factories.add(::ParticipantPredicate)
        preds.factories.add(::ThirdPartyNamesPredicate)
        predicates = preds.build()

        val acts = ObjFactory.Builder(RuleAction::class)
        acts.objectMapper = objectMapper
        acts.factories.add(::SetAttributeAction)
        acts.factories.add(this::createApplyRulesAction)
        actions = acts.build()
    }

    internal fun createApplyRulesAction(rulesNames: List<String>): ApplyRulesAction {
        val rules = rulesNames.map {
            loadById(it)
        }
        return ApplyRulesAction(rules)
    }

    fun getRules(): List<Rule<*>> {
        val entities = repository.findByEnabledTrueAndChildFalse()
        return entities.map { this.loadFromEntity(it) }
    }

    private fun loadById(ruleId: String): Rule<*> {
        val entity = repository.findByRuleId(ruleId)
        return loadFromEntity(entity!!)
    }

    private fun loadFromEntity(entity: RuleEntity): Rule<*> {
        if(!entity.enabled) {
            throw IllegalArgumentException("Rule '${entity.ruleId}' is disabled.")
        }
        val action: RuleAction<PredicateResult<*>> = if (entity.action == null) {
            RuleAction.NOP
        } else {
            actions.read(entity.action!!)
        }
        val predicate = predicates.read<RulePredicate<*>>(entity.predicate)
        return Rule(
                id = entity.ruleId,
                weight = entity.weight,
                predicate = predicate,
                action = action
        )
    }
}