package com.codeabovelab.tpc.tool.learn

import com.codeabovelab.tpc.core.nn.TokenizerFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.DirSentenceIterator
import com.codeabovelab.tpc.tool.util.Config
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
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
        //TODO learn on tagged words
        val iter = DirSentenceIterator(srcDir)
        val cache = AbstractCache<VocabWord>()
        val t = TokenizerFactoryImpl()
        val lc = LearnConfig()
        configure(lc)
        val pv = ParagraphVectors.Builder(lc.doc2vec)
                .iterate(iter)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .build()
        pv.fit()

        log.warn("Save learned data to $filePath.")
        WordVectorSerializer.writeParagraphVectors(pv, pvf)
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
