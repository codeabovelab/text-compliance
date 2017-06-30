package com.codeabovelab.tpc.doc

/**
 */
interface DocumentsRepository {
    /**
     * Retrieve document by id.
     * @return doc or null
     */
    operator fun get(id: String): Document?
}