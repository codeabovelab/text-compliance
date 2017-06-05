package com.codeabovelab.tpc.tool.learn

import com.codeabovelab.tpc.core.nn.TokenizerFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.DirSeqIterator
import com.codeabovelab.tpc.core.nn.nlp.Pos
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.slf4j.LoggerFactory
import java.io.File

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
        lc.configure(config)
        val ur = lc.createUimaResource()
        val ws = lc.wordSupplier()
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

}

