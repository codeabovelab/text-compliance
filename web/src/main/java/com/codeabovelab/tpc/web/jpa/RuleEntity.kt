package com.codeabovelab.tpc.web.jpa

import javax.persistence.Entity
import javax.persistence.Id

/**
 */
@Entity
class RuleEntity {
    @Id
    val id: Long = 0
    val weight: Float = 0f
    val predicate: String = ""
    val action: String? = null
}