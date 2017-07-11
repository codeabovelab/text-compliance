package com.codeabovelab.tpc.tool.rules

import org.springframework.data.jpa.repository.JpaRepository

/**
 */
interface RulesRepository: JpaRepository<RuleEntity, Long> {
}