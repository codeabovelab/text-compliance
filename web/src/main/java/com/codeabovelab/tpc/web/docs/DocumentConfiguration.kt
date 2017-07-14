package com.codeabovelab.tpc.web.docs

import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.doc.TextDocumentReader
import com.codeabovelab.tpc.integr.email.EmailDocumentReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 */
@Import(DocsStorage::class)
@Configuration
open class DocumentConfiguration {

    @Bean
    open fun documentReaders() = DocumentReaders.Builder()
            .set(EmailDocumentReader(), "email")
            .set(TextDocumentReader(), "text")
            .build()
}
