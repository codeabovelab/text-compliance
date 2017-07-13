package com.codeabovelab.tpc.web.jpa

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 */
@Entity
class RuleEntity {
    @Id
    @GeneratedValue
    var id: Long = 0
    var weight: Float = 0f
    var predicate: String = ""
    var action: String? = null
}