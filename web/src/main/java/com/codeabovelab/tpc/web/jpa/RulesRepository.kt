package com.codeabovelab.tpc.web.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 */
@Repository
interface RulesRepository: JpaRepository<RuleEntity, Long> {
    fun findByRuleId(ruleId: String): RuleEntity?
    fun deleteByRuleId(ruleId: String)
    fun findByEnabledTrueAndChildFalse(): List<RuleEntity>
}