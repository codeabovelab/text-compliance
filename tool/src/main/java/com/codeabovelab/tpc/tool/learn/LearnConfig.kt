package com.codeabovelab.tpc.tool.learn

import com.codeabovelab.tpc.core.nn.nlp.Pos
import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.nn.nlp.WordContext
import com.codeabovelab.tpc.tool.util.Config
import com.codeabovelab.tpc.tool.util.Copy
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths

/**
 */
class LearnConfig {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var root: Path? = null
    var doc2vec = VectorsConfiguration()
    var sentiment = SentimentConfiguration()
    var wordsConversion: WordConversion = WordConversion.RAW
    var thesaurus = ThesaurusConfiguration()

    init {
        doc2vec.minWordFrequency = 5
        doc2vec.iterations = 2
        doc2vec.epochs = 1
        doc2vec.layersSize = 100
        doc2vec.learningRate = 0.05
        doc2vec.isUseUnknown = true
        doc2vec.window = 7
        doc2vec.isTrainElementsVectors = true
        doc2vec.isTrainSequenceVectors = true
        // negative sample
        doc2vec.negative = 0.0
        doc2vec.sampling = 1E-5
    }

    fun createUimaRequest(): UimaFactory.Request {
        var p = false
        var m = false
        when(wordsConversion) {
            LearnConfig.WordConversion.LEMMA -> {
                m = true
            }
            LearnConfig.WordConversion.LEMMA_POS -> {
                m = true
                p = true
            }
            LearnConfig.WordConversion.POS -> {
                p = true
            }
            LearnConfig.WordConversion.RAW -> {
                //nothing
            }
        }
        return UimaFactory.Request(pos = p, morphological = m)
    }


    fun configure(config: Path?) {
        if (config == null) {
            return
        }
        val cf = config.toFile()
        if (cf.exists()) {
            log.info("Read config from $config")
            FileInputStream(cf).use {
                Config.read(this, it)
            }
        } else {
            save(config)
        }
        this.root = config.parent
    }

    fun save(config: Path) {
        log.info("Save config to $config")
        FileOutputStream(config.toFile()).use {
            Config.write(this, it)
        }
    }

    fun path(relativePath: String): Path {
        if(root == null) {
            throw IllegalStateException("Config is not bind with path, invoke 'configure()' first")
        }
        return root!!.resolve(relativePath)
    }


    fun wordSupplier(): (wc: WordContext) -> String? = when (wordsConversion) {
        LearnConfig.WordConversion.RAW -> {
            { it.word.str.toLowerCase() }
        }
        LearnConfig.WordConversion.POS -> {
            {
                if(it.word.pos == Pos.UNKNOWN)
                    it.word.str
                else
                    it.word.str + "_" + it.word.pos.kind
            }
        }
        LearnConfig.WordConversion.LEMMA -> {
            { it.word.lemma ?: it.word.str }
        }
        LearnConfig.WordConversion.LEMMA_POS -> {
            {
                val str = it.word.lemma ?: it.word.str
                if(it.word.pos == Pos.UNKNOWN)
                    str
                else
                    str + "_" + it.word.pos.kind
            }
        }
    }

    enum class WordConversion {
        /**
         * No conversion
         */
        RAW,
        /**
         * Add POS to end of word
         */
        POS,
        /**
         * Use lemma instead of word
         */
        LEMMA,
        /**
         * Use lemma + POS
         */
        LEMMA_POS
    }

    companion object {
        fun learnedDir(dir: String): Files {
            val root = Paths.get(dir)
            return Files(
                    root = root,
                    config = root.resolve(Config.FILE),
                    doc2vec = root.resolve("doc2vec.zip")
            )
        }
    }

    data class Files(
            var root: Path,
            val config: Path,
            val doc2vec: Path //move from root
    )

    data class ThesaurusConfiguration(
            var jwnlurl: String? = null,
            @Copy("words") var words: String? = null,
            @Copy("wordnet") var wordNet: String? = null
    )

    data class SentimentConfiguration(
            var batchSize: Int = 128, //Number of examples in each minibatch
            var vectorSize: Int = 300, //Size of the word vectors. 300 in the Google News model
            var nEpochs: Int = 2, //Number of epochs (full passes of training data) to train on
            var truncateReviewsToLength: Int = 256, //Truncate reviews with length (# words) greater than this
            var learningRate: Double = 3e-3,
            val modelFile: Path? = null,
            val wordVectorFile: Path? = null
    )
}

