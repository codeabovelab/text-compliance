package com.codeabovelab.tpc.tool.learn

import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

class SentimentDocumentFilesArray(val dataDirectory: String) : SentimentDocumentArray {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private var sentimentDocuments: List<SentimentDocumentFile> = ArrayList()

    init {
        reset()
    }

    override fun size(): Int {
        return sentimentDocuments.size
    }

    override operator fun get(cursor: Int): SentimentDocument {
        val sentimentDocumentFile = sentimentDocuments[cursor]
        return SentimentDocument(sentimentDocumentFile.file.readText(), sentimentDocumentFile.sentimentLabel)
    }

    override fun reset() {
        log.warn("Call reset on $dataDirectory")
        sentimentDocuments = Files.walk(Paths.get(dataDirectory))
                .filter { p -> p.toFile().isFile }
                .map { p ->
                    if (p.toString().contains("pos")) {
                        SentimentDocumentFile(p.toFile(), SentimentLabel.POSITIVE)
                    } else {
                        SentimentDocumentFile(p.toFile(), SentimentLabel.POSITIVE)
                    }
                }.collect(Collectors.toList())
    }

    data class SentimentDocumentFile(val file: File, val sentimentLabel: SentimentLabel)
}