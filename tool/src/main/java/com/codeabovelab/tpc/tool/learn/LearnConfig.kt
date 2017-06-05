package com.codeabovelab.tpc.tool.learn

import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration

/**
 */
class LearnConfig {
    var doc2vec = VectorsConfiguration()
    var wordsConversion: WordConversion = WordConversion.RAW

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
}

