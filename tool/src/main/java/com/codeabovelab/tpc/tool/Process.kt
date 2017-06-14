package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.kw.KeywordHashMatcher
import com.codeabovelab.tpc.core.kw.WordPredicate
import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.nn.TextClassifierResult
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.core.processor.ProcessModifier
import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.core.processor.Rule
import com.codeabovelab.tpc.core.thesaurus.JWNLWordSynonyms
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentField
import com.codeabovelab.tpc.doc.TextDocumentReader
import com.codeabovelab.tpc.integr.email.EmailDocumentReader
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.util.PathUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import net.didion.jwnl.JWNL
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors

/**
 */
class Process(
        private val inData: String,
        private val outData: String?,
        private val learned: String,
        private val words: String?,
        private val config: String?
) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val learnedDir = LearnConfig.learnedDir(learned)
    private val learnedConfig = LearnConfig()
    private val docReaders = mapOf(
            Pair("txt", TextDocumentReader()),
            Pair("eml", EmailDocumentReader())
    )
    private val om = ObjectMapper(YAMLFactory()).enable(SerializationFeature.INDENT_OUTPUT)
    private val outPath = Paths.get(outData ?: inData)
    private val inPath = Paths.get(inData)

    init {
        learnedConfig.configure(learnedDir.config)
        Files.createDirectories(outPath)
    }

    fun run() {

        val proc = Processor()
        configureTextClassifierProcessor(learnedDir, learnedConfig, proc)
        configureWordSearchProcessor(words, proc)

        val pathIter = Files.walk(inPath).filter {
            docReaders.containsKey(PathUtils.extension(it))
        }.iterator()
        while (pathIter.hasNext()) {
            val path = pathIter.next()
            try {
                processDoc(proc, path)
            } catch(e: Exception) {
                log.error("Fail on {}", path)
                throw e
            }
        }
    }

    private fun configureTextClassifierProcessor(ld: LearnConfig.Files, lc: LearnConfig, proc: Processor) {
        val tc = TextClassifier(
                vectorsFile = ld.doc2vec,
                maxLabels = 3,
                uima = lc.createUimaResource(),
                wordSupplier = lc.wordSupplier()
        )
        proc.addRule(Rule("classify", 0.0f, tc))
    }

    private fun configureWordSearchProcessor(words: String?, proc: Processor) {
        if (!words.isNullOrBlank()) {
            val wordSet = words!!.split(",").stream().collect(Collectors.toSet())
            if (!wordSet.isEmpty()) {
                initThesaurus()
                val thesaurus = JWNLWordSynonyms()
                val enrichedWords = wordSet.stream()
                        .flatMap { w -> thesaurus.lookup(w).words.stream() }
                        .collect(Collectors.toSet())
                val sw = WordPredicate(
                        keywordMatcher = KeywordHashMatcher(enrichedWords),
                        uima = SentenceIteratorImpl.uimaResource(morphological = true))
                proc.addRule(Rule("searchWords", 0.0f, sw))
            }
        }
    }

    private fun initThesaurus() {
        try {
            val resource = Paths.get(config)
            JWNL.initialize(resource.toFile().inputStream())
        } catch (e: Exception) {
            log.error("can't init Thesaurus", e)
        }
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