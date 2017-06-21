package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.kw.KeywordSetMatcher
import com.codeabovelab.tpc.core.kw.WordPredicate
import com.codeabovelab.tpc.core.kw.WordSearchResult
import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.nn.nlp.FileTextIterator
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.core.thesaurus.JWNLWordSynonyms
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
import net.didion.jwnl.JWNL
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.NumberFormat
import java.util.*

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
        configureTextClassifier(proc)
        configureKeyWord(proc)

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

    private fun configureTextClassifier(proc: Processor) {
        val tc = TextClassifier(
                vectorsFile = learnedDir.doc2vec,
                maxLabels = 3,
                uima = learnedConfig.createUimaResource(),
                wordSupplier = learnedConfig.wordSupplier()
        )
        proc.addRule(Rule("classify", 0.0f, tc))
    }

    private fun configureKeyWord(proc: Processor) {
        val thesaurusConfig = learnedConfig.thesaurus
        val keywordsDir = learnedConfig.path(thesaurusConfig.words)
        val hasKeywordsDir = Files.exists(keywordsDir)
        if (!hasKeywordsDir) {
            return
        }
        if(!initThesaurus(thesaurusConfig)) {
            return
        }
        val ksmBuilder = KeywordSetMatcher.Builder()
        if(hasKeywordsDir) {
            loadFromFiles(keywordsDir, ksmBuilder)
        }
        val sw = WordPredicate(
                keywordMatcher = ksmBuilder.build(),
                uima = SentenceIteratorImpl.uimaResource(morphological = true))
        proc.addRule(Rule("searchWords", 0.0f, sw))
    }

    private fun loadFromFiles(keywordsDir: Path, ksmBuilder: KeywordSetMatcher.Builder) {
        val thesaurus = JWNLWordSynonyms()
        Files.walk(keywordsDir).filter {
            "txt" == PathUtils.extension(it)
        }.forEach {
            val labels = FileTextIterator.extractLabels(it)
            Files.lines(it).forEach {
                ksmBuilder.add(it, labels)
                thesaurus.lookup(it).words.forEach {
                    ksmBuilder.add(it, labels)
                }
            }
        }
    }

    private fun initThesaurus(thesaurus: LearnConfig.ThesaurusConfiguration): Boolean {
        if(thesaurus.jwnlurl == null) {
            return false
        }
        val resource = learnedDir.root.resolve(thesaurus.jwnlurl!!)
        //below we use hack to define relative dir into JWNL xml config, wee need rewrite it
        val xml = resource.toFile().readText(StandardCharsets.UTF_8).replace("\${DIR}", learnedDir.root.toString())
        JWNL.initialize(ByteArrayInputStream(xml.toByteArray(StandardCharsets.UTF_8)))
        return true
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
        val labels = collectLabels(report)
        val numFormat = NumberFormat.getInstance(Locale.ROOT)
        log.info("{} labels: {}", relPath, labels.max.entries.joinToString {
            "${it.key}=${numFormat.format(it.value)} (min=${numFormat.format(labels.min[it.key])})"
        })
        val baseName = PathUtils.withoutExtension(relPath)
        val reportPath = outPath.resolve(baseName + "-report.yaml")
        val textPath = outPath.resolve(baseName + "-analyzed.txt")
        Files.createDirectories(reportPath.parent)
        Files.newBufferedWriter(textPath, StandardCharsets.UTF_8).use {
            printReport(labels, text, it)
        }
        Files.newOutputStream(reportPath).use {
            om.writeValue(it, report)
        }
    }

    private fun printReport(labels: LabelsData, text: StringBuilder, writer: BufferedWriter) {
        val numFormat = NumberFormat.getInstance(Locale.ROOT)
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
            writer.appendln()
        }
    }

    private fun collectLabels(report: ProcessorReport): LabelsData {
        val min = HashMap<String, Double>()
        val max = HashMap<String, Double>()
        val entries = HashMap<TextCoordinates, MutableSet<LabelEntry>>()
        for (rr in report.rules) {
            for (entry in rr.result.entries) {
                if (entry !is Labeled) {
                    continue
                }
                for (label in entry.labels) {
                    min.compute(label.label) { _, old ->
                        if (old == null) label.similarity else Math.min(old, label.similarity)
                    }
                    max.compute(label.label) { _, old ->
                        if (old == null) label.similarity else Math.max(old, label.similarity)
                    }
                    entries.compute(entry.coordinates) { _, old ->
                        val set = old ?: HashSet<LabelEntry>()
                        val notice = when(entry) {
                            is WordSearchResult.Entry -> "keyword=${entry.keywords.joinToString { it }}"
                            else -> "rule=${rr.ruleId}"
                        }
                        set.add(LabelEntry(
                                label,
                                notice
                        ))
                        set
                    }
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
            val db = reader.read(it)
            db.id = path.toString()
            return db.build()
        }
    }

    data class LabelsData(
            val min: Map<String, Double>,
            val max: Map<String, Double>,
            val entries: List<LabelsEntry>
    )

    data class LabelsEntry(
            val coordinates: TextCoordinates,
            val labels: Collection<LabelEntry>
    )

    data class LabelEntry(
            val label: Label,
            val notice: String
    )
}