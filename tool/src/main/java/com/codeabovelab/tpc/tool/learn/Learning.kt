package com.codeabovelab.tpc.tool.learn

import com.codeabovelab.tpc.core.nn.TokenizerFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.DirSeqIterator
import com.codeabovelab.tpc.core.nn.nlp.Pos
import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.tool.util.Config
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.deeplearning4j.text.uima.UimaResource
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 */
class Learning(
        private val srcDir: String,
        private val filePath: String,
        private val config: String?
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        log.info("Start learning on $srcDir, save data to $filePath.")
        val pvf = File(filePath)
        if (pvf.exists()) {
            log.warn("Destination $filePath is exists, do nothing.")
            return
        }
        val lc = LearnConfig()
        configure(lc)
        val ur = uimaResource(lc)
        val ws = wordSupplier(lc)
        val iter = DirSeqIterator(ur, srcDir, ws)
        val cache = AbstractCache<VocabWord>()
        val t = TokenizerFactoryImpl()
        val pv = ParagraphVectors.Builder(lc.doc2vec)
                //.iterate(iter)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .build()
        pv.setSequenceIterator(iter)
        pv.fit()

        log.warn("Save learned data to $filePath.")
        WordVectorSerializer.writeParagraphVectors(pv, pvf)
    }

    private fun wordSupplier(lc: LearnConfig): (wc: DirSeqIterator.WordContext) -> String? = when (lc.wordsConversion) {
        LearnConfig.WordConversion.RAW -> {
            { it.word.str }
        }
        LearnConfig.WordConversion.POS -> {
            {
                if(it.word.pos == Pos.UNKNOWN)
                    it.word.str
                else
                    it.word.str + "_" + it.word.pos
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
                    str + "_" + it.word.pos
            }
        }
    }

    private fun uimaResource(lc: LearnConfig): UimaResource {
        var p = false
        var m = false
        when(lc.wordsConversion) {
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
        return SentenceIteratorImpl.uimaResource(pos = p, morphological = m)
    }

    private fun configure(lc: LearnConfig) {
        if (config == null) {
            return
        }
        val cf = File(config)
        if (cf.exists()) {
            log.info("Read config from $config")
            FileInputStream(cf).use {
                Config.read(lc, it)
            }
        } else {
            log.info("Save config to $config")
            FileOutputStream(cf).use {
                Config.write(lc, it)
            }
        }
    }
}

