package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextImpl
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

    override val info = DocumentReader.Info(
            binary = false,
            type = "text"
    )

    override fun read(id: String?, istr: InputStream): DocumentImpl.Builder {
        val db = DocumentImpl.Builder()
        db.id = id
        InputStreamReader(istr, charset).use {
            db.body = TextImpl(CharStreams.toString(it))
        }
        return db
    }
}