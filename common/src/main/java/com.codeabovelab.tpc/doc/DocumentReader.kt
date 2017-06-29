package com.codeabovelab.tpc.doc

import java.io.InputStream

/**
 */
interface DocumentReader<out T : Document.Builder> {
    /**
     * read document from specified stream
     */
    fun read(id: String?, istr: InputStream): T
}