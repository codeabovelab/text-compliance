package com.codeabovelab.tpc.tool.process

import com.codeabovelab.tpc.core.kw.KeywordSetMatcher
import com.codeabovelab.tpc.core.kw.KeywordsFileHeader
import com.codeabovelab.tpc.core.kw.KeywordsFileReader
import com.codeabovelab.tpc.core.kw.WordPredicate
import com.codeabovelab.tpc.core.nn.TextClassifier
import com.codeabovelab.tpc.core.nn.nlp.FileTextIterator
import com.codeabovelab.tpc.core.nn.sentiment.SentimentClassifier
import com.codeabovelab.tpc.core.processor.RulePredicate
import com.codeabovelab.tpc.core.thesaurus.JwnlThesaurusDictionary
import com.codeabovelab.tpc.core.thesaurus.WordSynonyms
import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.util.PathUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.FileReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KCallable

/**
 * Provide some predefined predicates: keywords, classifier & etc
 */
class PredicateProvider(
        private val learnedDir: LearnConfig.Files,
        private val learnedConfig: LearnConfig
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val om = ObjectMapper()
    private val textClassifierPredicate = configureTextClassifier()
    private val sentimentClassifier = configureSentimentClassifier()
    private val wordPredicate = configureKeyWord()

    /**
     * Do not use this function. It need only for [publish], we make it public because consumer must has access
     * to it.
     */
    fun wordPredicateFactory() : WordPredicate {
        return wordPredicate!!
    }

    fun sentimentClassifier() : SentimentClassifier {
        return sentimentClassifier!!
    }

    fun publish(consumer: PredicateConsumer) {
        consumer(this::textClassifierPredicate)
        if (sentimentClassifier != null) {
            consumer(this::sentimentClassifier)
        }
        if(this.wordPredicate != null) {
            consumer(this::wordPredicateFactory)
        }
    }

    private fun configureSentimentClassifier(): SentimentClassifier? {
        if (learnedConfig.sentiment.modelFile == null ||
                learnedConfig.sentiment.wordVectorFile == null) {
            log.warn("SentimentClassifier is not configured and will be disabled, see: {}",
                    learnedConfig.sentiment)
            return null
        }
        return SentimentClassifier(
                modelFile = learnedConfig.sentiment.modelFile!!,
                wordVectorFile = learnedConfig.sentiment.wordVectorFile!!
        )
    }

    private fun configureTextClassifier(): TextClassifier {
        val tc = TextClassifier(
                vectorsFile = learnedDir.doc2vec,
                maxLabels = 3,
                wordSupplier = learnedConfig.wordSupplier()
        )
        return tc
    }

    private fun configureKeyWord() : WordPredicate? {
        val thesaurusConfig = learnedConfig.thesaurus
        if (thesaurusConfig.words == null) {
            return null
        }
        val keywordsDir = learnedConfig.path(thesaurusConfig.words!!)
        val hasKeywordsDir = Files.exists(keywordsDir)
        if (!hasKeywordsDir) {
            return null
        }
        val wordSynonyms = initThesaurus(thesaurusConfig)
        val ksmBuilder = KeywordSetMatcher.Builder()
        if (hasKeywordsDir) {
            loadFromFiles(keywordsDir, wordSynonyms, ksmBuilder)
        }
        val sw = WordPredicate(
                keywordMatcher = ksmBuilder.build()
        )
        return sw
    }


    private fun loadFromFiles(keywordsDir: Path, wordSynonyms: WordSynonyms, ksmBuilder: KeywordSetMatcher.Builder) {
        log.info("Begin scan directory {} for keyword files.", keywordsDir)
        val kfr = KeywordsFileReader(deserializer = { om.readValue(it, KeywordsFileHeader::class.java) })
        Files.walk(keywordsDir).filter {
            KeywordsFileReader.EXT == PathUtils.extension(it)
        }.forEach {
            try {
                val labels = FileTextIterator.extractLabels(it)
                kfr.read(FileReader(it.toFile())) { kfh, keyword ->
                    ksmBuilder.add(keyword, labels)
                    if(kfh.synonyms) {
                        wordSynonyms.lookup(keyword).words.forEach {
                            ksmBuilder.add(it, labels)
                        }
                    }
                }
            } catch (e: Exception) {
                log.error("Can not parse keyword file:", e)
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

typealias PredicateConsumer = (KCallable<RulePredicate<*>>) -> Unit