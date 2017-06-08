package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.Rule
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.tool.learn.LearnConfig
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

    init {
        learnedConfig.configure(learnedDir.config)
    }

    fun run() {

        val proc = Processor()
        configureProcessor(learnedDir, learnedConfig, proc)

        val pathIter = Files.walk(Paths.get(inData)).filter {
            val ext = it.toString().substringAfterLast('.')
            ext == "txt" || ext == "eml"
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
        val file = path.toFile()
        val str = com.google.common.io.Files.asCharSource(file, StandardCharsets.UTF_8).read()
        val doc = DocumentImpl.builder()
                .id(file.canonicalPath)
                .body(str)
                .build()
        val report = proc.process(doc)
        //TODO save to file
        log.info("Report {}", report)
    }
}