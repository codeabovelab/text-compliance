package com.codeabovelab.tpc.doc

import com.google.common.io.CharStreams
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 */
class TextDocumentReader(
        val charset: Charset = StandardCharsets.UTF_8
): DocumentReader<DocumentImpl.Builder> {

    override fun read(istr: InputStream): DocumentImpl.Builder {
        val db = DocumentImpl.builder()
        InputStreamReader(istr, charset).use {
            db.body = CharStreams.toString(it)
        }
        return db
    }
}