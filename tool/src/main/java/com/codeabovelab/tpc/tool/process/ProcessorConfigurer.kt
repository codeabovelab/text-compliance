package com.codeabovelab.tpc.tool.process

import com.codeabovelab.tpc.core.kw.KeywordSetMatcher
import com.codeabovelab.tpc.core.kw.KeywordsFileHeader
import com.codeabovelab.tpc.core.kw.KeywordsFileReader
import com.codeabovelab.tpc.core.kw.WordPredicate
import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.nn.nlp.FileTextIterator
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.core.processor.Processor
import com.codeabovelab.tpc.core.processor.Rule
import com.codeabovelab.tpc.core.thesaurus.JwnlThesaurusDictionary
import com.codeabovelab.tpc.core.thesaurus.WordSynonyms
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.util.PathUtils
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.ByteArrayInputStream
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 */
class ProcessorConfigurer(
        private val proc: Processor,
        private val learnedDir: LearnConfig.Files,
        private val learnedConfig: LearnConfig
) {

    val om = ObjectMapper()

    fun configure() {
        configureTextClassifier()
        configureKeyWord()
    }

    private fun configureTextClassifier() {
        val tc = TextClassifier(
                vectorsFile = learnedDir.doc2vec,
                maxLabels = 3,
                uima = learnedConfig.createUimaResource(),
                wordSupplier = learnedConfig.wordSupplier()
        )
        proc.addRule(Rule("classify", 0.0f, tc))
    }

    private fun configureKeyWord() {
        val thesaurusConfig = learnedConfig.thesaurus
        if (thesaurusConfig.words == null) {
            return
        }
        val keywordsDir = learnedConfig.path(thesaurusConfig.words!!)
        val hasKeywordsDir = Files.exists(keywordsDir)
        if (!hasKeywordsDir) {
            return
        }
        val wordSynonyms = initThesaurus(thesaurusConfig)
        val ksmBuilder = KeywordSetMatcher.Builder()
        if (hasKeywordsDir) {
            loadFromFiles(keywordsDir, wordSynonyms, ksmBuilder)
        }
        val sw = WordPredicate(
                keywordMatcher = ksmBuilder.build(),
                uima = SentenceIteratorImpl.uimaResource(morphological = true))
        proc.addRule(Rule("searchWords", 0.0f, sw))
    }


    private fun loadFromFiles(keywordsDir: Path, wordSynonyms: WordSynonyms, ksmBuilder: KeywordSetMatcher.Builder) {
        val kfr = KeywordsFileReader(deserializer = { om.readValue(it, KeywordsFileHeader::class.java) })
        Files.walk(keywordsDir).filter {
            KeywordsFileReader.EXT == PathUtils.extension(it)
        }.forEach {
            val labels = FileTextIterator.extractLabels(it)
            kfr.read(FileReader(it.toFile())) { kfh, keyword ->
                ksmBuilder.add(keyword, labels)
                if(kfh.synonyms) {
                    wordSynonyms.lookup(keyword).words.forEach {
                        ksmBuilder.add(it, labels)
                    }
                }
            }
        }
    }

    private fun initThesaurus(thesaurus: LearnConfig.ThesaurusConfiguration): WordSynonyms {
        if (thesaurus.jwnlurl == null) {
            return WordSynonyms(JwnlThesaurusDictionary.DictionaryResolver)
        } else {
            val resource = learnedDir.root.resolve(thesaurus.jwnlurl!!)
            //below we use hack to define relative dir into JWNL xml config, wee need rewrite it
            val xml = resource.toFile().readText(StandardCharsets.UTF_8).replace("\${DIR}", learnedDir.root.toString())
            return WordSynonyms(JwnlThesaurusDictionary.source(ByteArrayInputStream(xml.toByteArray(StandardCharsets.UTF_8))))
        }
    }
}