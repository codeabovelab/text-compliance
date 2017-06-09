package com.codeabovelab.tpc.doc

import java.io.InputStream

/**
 */
interface DocumentReader {
    /**
     * read document from specified stream
     */
    fun read(istr: InputStream): Document
}