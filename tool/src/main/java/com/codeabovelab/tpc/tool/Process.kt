package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.core.kw.KeywordSetMatcher
import com.codeabovelab.tpc.core.kw.WordPredicate
import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.nn.TextClassifierResult
import com.codeabovelab.tpc.core.nn.nlp.FileTextIterator
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.core.processor.ProcessModifier
import com.codeabovelab.tpc.core.processor.Processor
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
import java.io.ByteArrayInputStream
import java.io.StringReader
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
        val keywordsDir = learnedDir.keywords
        val thesaurusConfig = learnedConfig.thesaurus
        val wordsSet = thesaurusConfig.words.orEmpty()
        val hasKeywordsDir = Files.exists(keywordsDir)
        if (wordsSet.isEmpty() && !hasKeywordsDir) {
            return
        }
        if(!initThesaurus(thesaurusConfig)) {
            return
        }
        val thesaurus = JWNLWordSynonyms()
        val ksmBuilder = KeywordSetMatcher.Builder()
        val defaultLabel = Collections.singleton("key-word")
        wordsSet.forEach {
            ksmBuilder.add(it, defaultLabel)
            thesaurus.lookup(it).words.forEach {
                ksmBuilder.add(it, defaultLabel)
            }
        }
        if(hasKeywordsDir) {
            loadFromFiles(keywordsDir, ksmBuilder)
        }
        val sw = WordPredicate(
                keywordMatcher = ksmBuilder.build(),
                uima = SentenceIteratorImpl.uimaResource(morphological = true))
        proc.addRule(Rule("searchWords", 0.0f, sw))
    }

    private fun loadFromFiles(keywordsDir: Path, ksmBuilder: KeywordSetMatcher.Builder) {
        Files.walk(keywordsDir).filter {
            "txt" == PathUtils.extension(it)
        }.forEach {
            val labels = FileTextIterator.extractLabels(it)
            Files.lines(it).forEach {
                ksmBuilder.add(it, labels)
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
        val tcr = report.findRule<TextClassifierResult>()
        log.info("{} labels: {}", relPath, printLabels(tcr))
        val baseName = PathUtils.withoutExtension(relPath)
        val reportPath = outPath.resolve(baseName + "-report.yaml")
        val textPath = outPath.resolve(baseName + "-analyzed.txt")
        Files.createDirectories(reportPath.parent)
        if(tcr != null) {
            val numFormat = NumberFormat.getInstance()
            Files.newBufferedWriter(textPath, StandardCharsets.UTF_8).use {
                for(entry in tcr.entries) {
                    val coords = entry.coordinates
                    it.append(text, coords.offset, coords.offset + coords.length)
                    for(label in entry.labels) {
                        it.append("\n \u26A0 ")
                        it.append(label.label)
                        it.append('=')
                        it.append(numFormat.format(label.similarity))
                    }
                    it.appendln()
                }
            }
        }
        Files.newOutputStream(reportPath).use {
            om.writeValue(it, report)
        }
    }

    private fun printLabels(tcr: TextClassifierResult?): String {
        if(tcr == null) {
            return ""
        }
        val map = HashMap<String, Double>()
        for(entry in tcr.entries) {
            for(label in entry.labels) {
                map.compute(label.label) { _, old ->
                    if(old == null) label.similarity else Math.min(old, label.similarity)
                }
            }
        }
        return tcr.labels.joinToString { "${it.label}=${it.similarity} (min=${map[it.label]})" }
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