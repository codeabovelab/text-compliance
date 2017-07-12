package com.codeabovelab.tpc.tool.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 */
@Repository
interface DocsRepository : JpaRepository<DocEntity, Long> {

    fun findByDocumentId(docId: String): DocEntity?
}