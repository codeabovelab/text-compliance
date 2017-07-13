package com.codeabovelab.tpc.web.jpa

import org.springframework.data.jpa.repository.JpaRepository

/**
 */
interface RulesRepository: JpaRepository<RuleEntity, Long> {
}