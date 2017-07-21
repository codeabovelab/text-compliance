package com.codeabovelab.tpc.web.jpa

import java.time.LocalDateTime
import javax.persistence.*

/**
 */
@Entity
class ProcessorReportEntity {
    @Id
    @GeneratedValue
    var id: Long = 0
    @Column(nullable = false)
    lateinit var date: LocalDateTime
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    lateinit var document: DocEntity
    @Column(nullable = false, length = 10240 * 4)
    lateinit var data: String
}