package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.Rule
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.doc.TextDocumentReader
import com.codeabovelab.tpc.integr.email.EmailDocumentReader
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.util.PathUtils
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 */
class Process(
        private val inData: String,
        private val learned: String
) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val learnedDir = LearnConfig.learnedDir(learned)
    private val learnedConfig = LearnConfig()
    private val docReaders = mapOf(
            Pair("txt", TextDocumentReader()),
            Pair("eml", EmailDocumentReader())
    )

    init {
        learnedConfig.configure(learnedDir.config)
    }

    fun run() {

        val proc = Processor()
        configureProcessor(learnedDir, learnedConfig, proc)

        val pathIter = Files.walk(Paths.get(inData)).filter {
            docReaders.containsKey(PathUtils.extension(it))
        } .iterator()
        while(pathIter.hasNext()) {
            val path = pathIter.next()
            log.info("Process {}", path)
            processDoc(proc, path)
        }
    }

    private fun configureProcessor(ld: LearnConfig.Files, lc: LearnConfig, proc: Processor) {
        val tc = TextClassifier(
                vectorsFile = ld.doc2vec,
                maxLabels = 3,
                uima = lc.createUimaResource(),
                wordSupplier = lc.wordSupplier()
        )
        proc.addRule(Rule("classify", 0.0f, tc))
    }

    private fun processDoc(proc: Processor, path: Path) {
        val doc = readDoc(path)
        val report = proc.process(doc)
        //TODO save to file
        log.info("Report {}", report)
    }

    private fun readDoc(path: Path): Document {
        val ext = PathUtils.extension(path)
        val reader = docReaders[ext]!!
        Files.newInputStream(path).use {
            return reader.read(it).build()
        }
    }
}