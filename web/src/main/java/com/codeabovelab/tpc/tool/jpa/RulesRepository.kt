package com.codeabovelab.tpc.tool.jpa

import org.springframework.data.jpa.repository.JpaRepository

/**
 */
interface RulesRepository: JpaRepository<RuleEntity, Long> {
}