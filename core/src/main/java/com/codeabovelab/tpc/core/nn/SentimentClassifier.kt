package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.text.Text
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import java.nio.file.Path
import java.util.*

/**
 */
@JsonTypeName("SentimentClassifierPredicate")
class SentimentClassifier(
        modelFile: Path,
        wordVectorFile: Path,
        val truncateReviewsToLength: Int
) : RulePredicate<SentimentClassifierResult> {

    private val net: MultiLayerNetwork
    private val tokenizerFactory: TokenizerFactory
    private val wordVectors: WordVectors
    private val vectorSize: Int

    init {
        net = ModelSerializer.restoreMultiLayerNetwork(modelFile.toFile())
        tokenizerFactory = DefaultTokenizerFactory()
        tokenizerFactory.setTokenPreProcessor(CommonPreprocessor())
        wordVectors = WordVectorSerializer.loadStaticModel(wordVectorFile.toFile())
        vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).size
    }

    override fun test(pc: PredicateContext, text: Text): SentimentClassifierResult {

        val features = loadFeaturesFromString(text.data.toString(), truncateReviewsToLength)
        val networkOutput = net.output(features)
        val timeSeriesLength = networkOutput.size(2)
        val probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1))

        val positiveProbability = probabilitiesAtLastWord.getDouble(0)
        val negativeProbability = probabilitiesAtLastWord.getDouble(1)
        if (negativeProbability > positiveProbability) {
            return SentimentClassifierResult(Collections.singleton(Label("negative", negativeProbability)))
        }
        return SentimentClassifierResult(Collections.emptyList())

    }

    fun loadFeaturesFromString(reviewContents: String, maxLength: Int): INDArray {
        val tokens = tokenizerFactory.create(reviewContents).tokens
        val tokensFiltered = tokens.filter { wordVectors.hasWord(it) }
        val outputLength = Math.max(maxLength, tokensFiltered.size)

        val features = Nd4j.create(1, vectorSize, outputLength)

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
        /**
         * Labels for all text
         */
        override val labels: Collection<Label>
) : Labeled, PredicateResult<PredicateResult.Entry>(listOf()) {

    override fun isEmpty(): Boolean {
        return labels.isEmpty()
    }
}