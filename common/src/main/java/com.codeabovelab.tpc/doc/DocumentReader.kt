package com.codeabovelab.tpc.doc

import java.io.InputStream

/**
 */
interface DocumentReader<T : Document.Builder<T>> {
    /**
     * read document from specified stream
     */
    fun read(id: String?, istr: InputStream): T

    val info: Info

    data class Info(
            val binary: Boolean,
            val type: String
    )
}