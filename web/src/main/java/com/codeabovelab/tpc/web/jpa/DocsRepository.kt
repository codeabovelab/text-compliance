package com.codeabovelab.tpc.web.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 */
@Repository
interface DocsRepository : JpaRepository<DocEntity, Long> {

    fun findByDocumentId(docId: String): DocEntity?
    fun deleteByDocumentId(docId: String)
}