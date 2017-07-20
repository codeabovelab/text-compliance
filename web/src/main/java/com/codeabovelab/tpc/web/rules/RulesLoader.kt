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

    private val predicateObjs : ObjUri<RulePredicate<*>>
    private val actionObjs = ObjUri<RuleAction<*>>()

    init {
        val lc = LearnConfig()
        val learnedDir = LearnConfig.learnedDir(classifierDir)
        lc.configure(learnedDir.config)
        val pp = PredicateProvider(
                learnedDir = learnedDir,
                learnedConfig = lc
        )
        val builder = ClassScheme.Builder<RulePredicate<*>>()
        pp.publish {
            builder.factories.add(it)
        }
        builder.factories.add(::RegexPredicate)
        builder.factories.add(::ParticipantPredicate)
        predicateObjs = ObjUri(builder.build())
    }

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