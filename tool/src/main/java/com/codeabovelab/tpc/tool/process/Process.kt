package com.codeabovelab.tpc.tool.process

import com.codeabovelab.tpc.core.kw.WordSearchResult
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentField
import com.codeabovelab.tpc.doc.TextDocumentReader
import com.codeabovelab.tpc.integr.email.EmailDocumentReader
import com.codeabovelab.tpc.text.TextCoordinates
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.util.PathUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.NumberFormat
import java.util.*

/**
 */
class Process(
        inData: String,
        outData: String?,
        learned: String
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
        val proc = Processor(
                sentenceIteratorFactory = SentenceIteratorFactoryImpl(UimaFactory.create(morphological = true))
        )
        PredicateProvider(
                learnedConfig = learnedConfig,
                learnedDir = learnedDir
        ).publish {
            val predicate = it.call()
            proc.addRule(Rule(id = it.name, weight = 1f, predicate = predicate))
        }

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

    private fun processDoc(proc: Processor, path: Path) {
        val doc = readDoc(path)
        val text = StringBuilder()
        val modifier = ProcessModifier(
                filter = { it !is DocumentField },
                textHandler = {
                    text.append(it.data)
                    it
                }
        )
        val report = proc.process(doc, modifier)

        val relPath = if (path == inPath) path.fileName else inPath.relativize(path)
        val numFormat = NumberFormat.getInstance(Locale.ROOT)
        log.info("{} labels: {}", relPath, report.labels.joinToString {
            "${it.label}=${numFormat.format(it.similarity)}"
        })
        val baseName = PathUtils.withoutExtension(relPath)
        val reportPath = outPath.resolve(baseName + "-report.yaml")
        val textPath = outPath.resolve(baseName + "-analyzed.txt")
        Files.createDirectories(reportPath.parent)
        Files.newBufferedWriter(textPath, StandardCharsets.UTF_8).use {
            printReport(report, text, it)
        }
        Files.newOutputStream(reportPath).use {
            om.writeValue(it, report)
        }
    }

    private fun printReport(report: ProcessorReport, text: StringBuilder, writer: BufferedWriter) {
        val labels = collectLabels(report)
        val numFormat = NumberFormat.getInstance(Locale.ROOT)
        writer.appendln("LABELS:")
        for (label in report.labels) {
            writer.append(label.label)
            writer.append('=')
            writer.append(numFormat.format(label.similarity))
            writer.newLine()
        }
        writer.appendln("ENTRIES:")
        for (entry in labels.entries) {
            val coords = entry.coordinates
            writer.append(text, coords.offset, coords.offset + coords.length)
            for (label in entry.labels) {
                writer.append("\n \u26A0 ")
                writer.append(label.label.label)
                writer.append('=')
                writer.append(numFormat.format(label.label.similarity))
                writer.append(" notice: ")
                writer.append(label.notice)
            }
            writer.newLine()
        }
    }

    private fun collectLabels(report: ProcessorReport): LabelsData {
        val min = HashMap<String, Double>()
        val max = HashMap<String, Double>()
        val entries = HashMap<TextCoordinates, MutableSet<LabelEntry>>()
        report.visitEntries { entry ->
            if(entry !is Labeled) {
                return@visitEntries
            }
            for(label in entry.labels) {
                min.compute(label.label) { _, old ->
                    if (old == null) label.similarity else Math.min(old, label.similarity)
                }
                max.compute(label.label) { _, old ->
                    if (old == null) label.similarity else Math.max(old, label.similarity)
                }
                entries.compute(entry.coordinates) { _, old ->
                    val set = old ?: HashSet()
                    val notice = when (entry) {
                        is WordSearchResult.Entry -> "keyword=${entry.keywords.joinToString { it }}"
                        else -> "entry=${entry}"
                    }
                    set.add(LabelEntry(
                            label,
                            notice
                    ))
                    set
                }
            }
        }
        return LabelsData(
                min = min,
                max = max,
                entries = entries.map { LabelsEntry(it.key, it.value) }
        )
    }

    private fun readDoc(path: Path): Document {
        val ext = PathUtils.extension(path)
        val reader = docReaders[ext]!!
        Files.newInputStream(path).use {
            val db = reader.read(path.toString(), it)
            return db.build()
        }
    }

}