package com.codeabovelab.tpc.web.jpa

import javax.persistence.Column
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

    @Column(unique = true, nullable = false)
    lateinit var ruleId: String

    @Column(nullable = false)
    var weight: Float = 1f

    @Column(length = 10240, nullable = false)
    lateinit var predicate: String

    @Column(length = 10240)
    var action: String? = null

    @Column(length = 1024)
    var description: String? = null
    /**
     * Flag which enable rule. Default true.
     */
    var enabled: Boolean = true
    /**
     * Flag which disable rule on top level. Child rule can be applied
     * only when called from another rule.
     */
    var child: Boolean = true
}