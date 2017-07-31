package com.codeabovelab.tpc.web.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 */
@Repository
interface ProcessorReportsRepository : JpaRepository<ProcessorReportEntity, Long> {

    @Query("select r from #{#entityName} as r join r.document d where d.documentId = :documentId order by r.date desc")
    fun getReportsByDocument(@Param("documentId") documentId: String): List<ProcessorReportEntity>
}