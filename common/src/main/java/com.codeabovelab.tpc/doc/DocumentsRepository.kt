package com.codeabovelab.tpc.doc

/**
 */
interface DocumentsRepository {
    operator fun get(id: String): Document
}