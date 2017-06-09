package com.codeabovelab.tpc.doc

import java.io.InputStream

/**
 */
interface DocumentReader<out T : Document.Builder> {
    /**
     * read document from specified stream
     */
    fun read(istr: InputStream): T
}