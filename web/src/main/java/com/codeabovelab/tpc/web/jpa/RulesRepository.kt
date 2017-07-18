package com.codeabovelab.tpc.web.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 */
@Repository
interface RulesRepository: JpaRepository<RuleEntity, Long> {
}