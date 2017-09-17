package com.codeabovelab.tpc.core.nn.sentiment

import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import com.codeabovelab.tpc.text.TextCoordinatesImpl
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 */
@JsonTypeName("SentimentClassifierPredicate")
class SentimentClassifier(
        modelFile: Path,
        wordVectorFile: Path,
        private val truncateReviewsToLength: Int = 256,
        private val precission: Double = 0.2
) : RulePredicate<SentimentClassifierResult> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val net: MultiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(modelFile.toFile())
    private val tokenizerFactory: TokenizerFactory
    private val wordVectors: WordVectors
    private val vectorSize: Int

    init {
//        tokenizerFactory = DefaultTokenizerFactory()
        tokenizerFactory = UimaTokenizerFactoryF(UimaFactory.create(morphological = true, pos = false))
        tokenizerFactory.setTokenPreProcessor(CommonPreprocessor())
        wordVectors = WordVectorSerializer.loadStaticModel(wordVectorFile.toFile())
        vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).size
        log.info("SentimentClassifier inited with modelFile={}, wordVectorFile={}", modelFile, wordVectorFile)
    }

    override fun test(pc: PredicateContext, text: Text): SentimentClassifierResult {

        val features = loadFeaturesFromString(text.data.toString(), truncateReviewsToLength)
        val networkOutput = net.output(features)
        val timeSeriesLength = networkOutput.size(2)
        val probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(),
                NDArrayIndex.point(timeSeriesLength - 1))

        val positiveProbability = probabilitiesAtLastWord.getDouble(0)
        val negativeProbability = probabilitiesAtLastWord.getDouble(1)
        log.debug("result {positive=$positiveProbability, negative=$negativeProbability} for '$text'")
        val labels = listOf(Label("negative", negativeProbability))
        return SentimentClassifierResult(
                labels = labels,
                entries = listOf(SentimentClassifierResult.Entry(coordinates = TextCoordinatesImpl(0, text.length),
                        labels = labels))
        )
        return SentimentClassifierResult(emptyList(), emptyList())
    }

    private fun loadFeaturesFromString(reviewContents: String, maxLength: Int): INDArray {
        val tokens = tokenizerFactory.create(reviewContents).tokens
        val tokensFiltered = tokens.filter { wordVectors.hasWord(it) }
        val maxLength = Math.min(maxLength, tokensFiltered.size)

        val features = Nd4j.create(1, vectorSize, maxLength)

        var j = 0
        while (j < tokens.size && j < maxLength) {
            val token = tokens[j]
            val vector = wordVectors.getWordVectorMatrix(token)
            features.put(arrayOf(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)), vector)
            j++
        }
        return features
    }

}

class SentimentClassifierResult(
        entries: List<Entry>,
        override val labels: Collection<Label>
) : PredicateResult<SentimentClassifierResult.Entry>(entries), Labeled {

    class Entry(
            coordinates: TextCoordinates,
            override val labels: List<Label>
    ) : PredicateResult.Entry(coordinates), Labeled {
        override fun toString(): String {
            return "Entry(labels=$labels)"
        }
    }

    override fun toString(): String {
        return "SentimentClassifierResult(labels=$labels)"
    }


}
