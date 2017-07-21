package com.codeabovelab.tpc.web.jpa

import javax.persistence.*

/**
 */
@Entity
open class DocEntity {
    @Id
    @GeneratedValue
    var id: Long = 0

    @Column(nullable = false)
    lateinit var type: String
    @Column(unique = true, nullable = false)
    lateinit var documentId: String
    /**
     * Raw data of document
     */
    @Column(length = 10240, nullable = false)
    lateinit var data: ByteArray
    var binary: Boolean = true
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    lateinit var reports: List<ProcessorReportEntity>
}