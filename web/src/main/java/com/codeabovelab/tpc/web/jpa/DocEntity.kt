package com.codeabovelab.tpc.web.jpa

import javax.persistence.*

/**
 */
@Entity
open class DocEntity {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var type: String
    @Column(unique = true)
    lateinit var documentId: String
    /**
     * Raw data of document
     */
    @Column(length = 10240)
    lateinit var data: ByteArray
    var binary: Boolean = true
    @Column(length = 10240 * 4)
    var report: String? = null
}