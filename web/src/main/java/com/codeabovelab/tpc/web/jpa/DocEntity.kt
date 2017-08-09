package com.codeabovelab.tpc.web.jpa

import java.time.LocalDateTime
import javax.persistence.*

/**
 */
@Entity
open class DocEntity {

    companion object {
        const val MAX_DOC_SIZE = 20 * 1024 * 1024
    }

    @Id
    @GeneratedValue
    var id: Long = 0

    /**
     * mime-type string
     */
    @Column(nullable = false)
    lateinit var type: String
    /**
     * original filename, need for download
     */
    @Column(nullable = true)
    var filename: String? = null
    @Column(unique = true, nullable = false)
    lateinit var documentId: String
    @Column(nullable = false)
    lateinit var date: LocalDateTime
    /**
     * Raw data of document
     */
    @Column(length = MAX_DOC_SIZE, nullable = false)
    lateinit var data: ByteArray
    var binary: Boolean = true
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    lateinit var reports: MutableList<ProcessorReportEntity>
}