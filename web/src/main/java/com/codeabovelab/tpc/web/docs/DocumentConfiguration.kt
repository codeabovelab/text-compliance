package com.codeabovelab.tpc.web.docs

import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.doc.TextDocumentReader
import com.codeabovelab.tpc.integr.email.EmailDocumentReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 */
@Import(
        DocsStorage::class,
        ThreadResolverService::class
)
@Configuration
class DocumentConfiguration {

    @Bean
    fun documentReaders() = DocumentReaders.Builder()
            .set(EmailDocumentReader(), "email")
            .set(TextDocumentReader(), "text")
            .build()
}
