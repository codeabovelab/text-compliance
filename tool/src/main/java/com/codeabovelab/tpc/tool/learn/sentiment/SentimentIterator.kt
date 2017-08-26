package com.codeabovelab.tpc.tool.learn.sentiment

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.DataSetPreProcessor
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import java.io.IOException
import java.util.*

class SentimentIterator(
        private val wordVectors: WordVectors,
        private val batchSize: Int,
        private val textIterator: SentimentDocumentArray,
        private val truncateLength: Int) : DataSetIterator {

    private val vectorSize: Int
    private var cursor = 0
    private val tokenizerFactory: TokenizerFactory

    init {
        this.vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).size

        tokenizerFactory = DefaultTokenizerFactory()
        tokenizerFactory.setTokenPreProcessor(CommonPreprocessor())
    }

    override fun getLabels(): List<String> {
        return listOfNotNull(SentimentLabel.NEGATIVE.name.toLowerCase(), SentimentLabel.POSITIVE.name.toLowerCase())
    }

    override fun cursor(): Int {
        return cursor
    }

    override fun remove() {
    }

    override fun inputColumns(): Int {
        return vectorSize
    }

    override fun numExamples(): Int {
        return totalExamples()
    }

    override fun batch(): Int {
        return batchSize
    }

    override fun next(num: Int): DataSet {
        if (cursor >= totalExamples()) throw NoSuchElementException()
        try {
            return nextDataSet(num)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun nextDataSet(num: Int): DataSet {
        //First: load reviews to String. Alternate positive and negative reviews
        val reviews = ArrayList<String>(num)
        val positive = BooleanArray(num)

        run {
            var i = 0
            while (i < num && cursor < totalExamples()) {
                val sentence = textIterator[i]
                reviews.add(sentence.text)
                positive[i] = sentence.label == SentimentLabel.POSITIVE
                cursor++
                i++
            }
        }

        //Second: tokenize reviews and filter out unknown words
        val allTokens = ArrayList<List<String>>(reviews.size)
        var maxLength = 0
        for (s in reviews) {
            val tokens = tokenizerFactory.create(s).tokens
            val tokensFiltered = ArrayList<String>()
            for (t in tokens) {
                if (wordVectors.hasWord(t)) tokensFiltered.add(t)
            }
            allTokens.add(tokensFiltered)
            maxLength = Math.max(maxLength, tokensFiltered.size)
        }

        //If longest review exceeds 'truncateLength': only take the first 'truncateLength' words
        if (maxLength > truncateLength) maxLength = truncateLength

        //Create data for training
        //Here: we have reviews.size() examples of varying lengths
        val features = Nd4j.create(intArrayOf(reviews.size, vectorSize, maxLength), 'f')
        val labels = Nd4j.create(intArrayOf(reviews.size, 2, maxLength), 'f')    //Two labels: positive or negative
        //Because we are dealing with reviews of different lengths and only one output at the final time step: use padding arrays
        //Mask arrays contain 1 if data is present at that time step for that example, or 0 if data is just padding
        val featuresMask = Nd4j.zeros(reviews.size, maxLength)
        val labelsMask = Nd4j.zeros(reviews.size, maxLength)

        val temp = IntArray(2)
        for (i in reviews.indices) {
            val tokens = allTokens[i]
            temp[0] = i
            //Get word vectors for each word in review, and put them in the training data
            var j = 0
            while (j < tokens.size && j < maxLength) {
                val token = tokens[j]
                val vector = wordVectors.getWordVectorMatrix(token)
                features.put(arrayOf(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j)), vector)

                temp[1] = j
                featuresMask.putScalar(temp, 1.0)  //Word is present (not padding) for this example + time step -> 1.0 in features mask
                j++
            }

            val idx = if (positive[i]) 0 else 1
            val lastIdx = Math.min(tokens.size, maxLength)
            labels.putScalar(intArrayOf(i, idx, lastIdx - 1), 1.0)   //Set label: [0,1] for negative, [1,0] for positive
            labelsMask.putScalar(intArrayOf(i, lastIdx - 1), 1.0)   //Specify that an output exists at the final time step for this example
        }

        return DataSet(features, labels, featuresMask, labelsMask)

    }

    override fun next(): DataSet {
        return next(batchSize)
    }

    override fun totalOutcomes(): Int {
        return 2
    }

    override fun setPreProcessor(preProcessor: DataSetPreProcessor?) {
        throw UnsupportedOperationException()
    }

    override fun totalExamples(): Int {
        return textIterator.size()
    }

    override fun reset() {
        cursor = 0
        textIterator.reset()
    }

    override fun hasNext(): Boolean {
        return cursor < numExamples()
    }

    override fun getPreProcessor(): DataSetPreProcessor {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun asyncSupported(): Boolean {
        return true
    }

    override fun resetSupported(): Boolean {
        return true
    }

}
