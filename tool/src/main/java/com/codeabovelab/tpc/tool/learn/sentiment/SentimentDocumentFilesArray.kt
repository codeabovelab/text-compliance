package com.codeabovelab.tpc.tool.learn.sentiment

import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class SentimentDocumentFilesArray(private val dataDirectory: Path, private val label: SentimentLabel) : SentimentDocumentArray {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private var sentimentDocuments: List<SentimentDocumentFile> = ArrayList()

    init {
        sentimentDocuments = Files.walk(dataDirectory)
                .filter { p -> p.toFile().isFile }
                .map { p -> SentimentDocumentFile(p.toFile(), label) }
                .collect(Collectors.toList())
        log.warn("documents scanned: {} {} at {}", sentimentDocuments,size(), label, dataDirectory)
    }

    override fun size(): Int {
        return sentimentDocuments.size
    }

    override operator fun get(cursor: Int): SentimentDocument {
        val sentimentDocumentFile = sentimentDocuments[cursor]
        return SentimentDocument(sentimentDocumentFile.file.readText(), sentimentDocumentFile.sentimentLabel)
    }

    data class SentimentDocumentFile(val file: File, val sentimentLabel: SentimentLabel)
}