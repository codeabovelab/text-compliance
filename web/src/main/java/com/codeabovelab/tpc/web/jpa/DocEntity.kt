package com.codeabovelab.tpc.web.jpa

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 */
@Entity
open class DocEntity {
    @Id
    var id: Long = 0
    var type: String = ""
    @Column(unique = true)
    var documentId: String = ""
    /**
     * Raw data of document
     */
    var data: ByteArray = byteArrayOf()
    var binary: Boolean = true
}