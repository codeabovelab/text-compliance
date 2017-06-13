package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.nn.TextClassifierResult
import com.codeabovelab.tpc.core.processor.ProcessModifier
import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.core.processor.Rule
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentField
import com.codeabovelab.tpc.doc.TextDocumentReader
import com.codeabovelab.tpc.integr.email.EmailDocumentReader
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.util.PathUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 */
class Process(
        private val inData: String,
        private val outData: String?,
        private val learned: String
) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val learnedDir = LearnConfig.learnedDir(learned)
    private val learnedConfig = LearnConfig()
    private val docReaders = mapOf(
            Pair("txt", TextDocumentReader()),
            Pair("eml", EmailDocumentReader())
    )
    private val om = ObjectMapper(YAMLFactory()).enable(SerializationFeature.INDENT_OUTPUT)
    private val outPath = Paths.get(outData?: inData)
    private val inPath = Paths.get(inData)
    init {
        learnedConfig.configure(learnedDir.config)
        Files.createDirectories(outPath)
    }

    fun run() {

        val proc = Processor()
        configureProcessor(learnedDir, learnedConfig, proc)

        val pathIter = Files.walk(inPath).filter {
            docReaders.containsKey(PathUtils.extension(it))
        } .iterator()
        while(pathIter.hasNext()) {
            val path = pathIter.next()
            try {
                processDoc(proc, path)
            } catch(e: Exception) {
                log.error("Fail on {}", path)
                throw e
            }
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
        val lines = ArrayList<String>()
        val modifier = ProcessModifier(
                filter = { it !is DocumentField },
                textHandler = {
                    lines += it.data.toString().replace("\n", " ")
                    it
                }
        )
        val report = proc.process(doc, modifier)
        val relPath = if (path == inPath) path.fileName else inPath.relativize(path)
        log.info("{} labels: {}", relPath, printLabels(report))
        val baseName = PathUtils.withoutExtension(relPath)
        val reportPath = outPath.resolve(baseName + "-report.yaml")
        val textPath = outPath.resolve(baseName + "-analyzed.txt")
        Files.createDirectories(reportPath.parent)
        Files.write(textPath, lines, StandardCharsets.UTF_8)
        Files.newOutputStream(reportPath).use {
            om.writeValue(it, report)
        }
    }

    private fun printLabels(report: ProcessorReport): String {
        val labels = report.findRule<TextClassifierResult>()?.labels ?: return ""
        return labels.joinToString { "${it.label}=${it.similarity}" }
    }

    private fun readDoc(path: Path): Document {
        val ext = PathUtils.extension(path)
        val reader = docReaders[ext]!!
        Files.newInputStream(path).use {
            val db = reader.read(it)
            db.id = path.toString()
            return db.build()
        }
    }
}