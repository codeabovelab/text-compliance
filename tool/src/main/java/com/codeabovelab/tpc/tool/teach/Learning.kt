package com.codeabovelab.tpc.tool.teach

import com.codeabovelab.tpc.core.nn.TokenizerFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.DirSentenceIterator
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
        private val filePath: String
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        log.info("Start learning on $srcDir, save data to $filePath.")
        val pvf = File(filePath)
        if(pvf.exists()) {
            log.warn("Destination $filePath is exists, do nothing.")
            return
        }
        val iter = DirSentenceIterator(srcDir)
        //TODO learn on tagged words
        val cache = AbstractCache<VocabWord>()
        val t = TokenizerFactoryImpl()
        var pv = ParagraphVectors.Builder()
                .minWordFrequency(5)
                .iterations(2)
                .epochs(1)
                .layerSize(100)
                .learningRate(0.05)
                .useUnknown(true)
                .windowSize(7)
                .iterate(iter)
                .trainWordVectors(true)
                .trainSequencesRepresentation(true)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .negativeSample(0.0)
                .sampling(1E-5)
                .build()

        pv.fit()

        log.warn("Save learned data to $filePath.")
        WordVectorSerializer.writeParagraphVectors(pv, pvf)
    }
}
