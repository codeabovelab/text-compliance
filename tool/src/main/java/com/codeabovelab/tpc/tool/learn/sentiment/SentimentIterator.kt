package com.codeabovelab.tpc.tool.learn.sentiment

import com.codeabovelab.tpc.core.nn.nlp.UimaFactory
import com.codeabovelab.tpc.core.nn.sentiment.UimaTokenizerFactoryF
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.DataSetPreProcessor
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import java.nio.file.Path
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class SentimentIterator(
        private val wordVectors: WordVectors,
        private val batchSize: Int,
        private val dataPath: Path,
        private val truncateLength: Int) : DataSetIterator {

    private var cursor = AtomicInteger()
    private val vectorSize: Int = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).size
    private val tokenizerFactory: TokenizerFactory
    private val textIteratorPositive = SentimentDocumentFilesArray(dataPath.resolve("pos"), SentimentLabel.POSITIVE)
    private val textIteratorNegative = SentimentDocumentFilesArray(dataPath.resolve("neg"), SentimentLabel.NEGATIVE)

    init {
//        tokenizerFactory = DefaultTokenizerFactory()
        tokenizerFactory = UimaTokenizerFactoryF(UimaFactory.create(morphological = true, pos = false))
        tokenizerFactory.setTokenPreProcessor(CommonPreprocessor())
    }

    override fun getLabels(): List<String> {
        return listOfNotNull(SentimentLabel.NEGATIVE.name.toLowerCase(), SentimentLabel.POSITIVE.name.toLowerCase())
    }

    override fun cursor(): Int {
        return cursor.get()
    }

    override fun remove() {}

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
        val cur = cursor.addAndGet(num)
        val last = Math.min(cur, totalExamples())
        val first = cur - num
        if (last - first < 1) throw NoSuchElementException()
        return nextDataSet(first, last)
    }

    private fun nextDataSet(first: Int, last: Int): DataSet {
        //First: load documents to String. Alternate positive and negative documents
        val documents = loadDocuments(first, last)

        //Second: tokenize documents and filter out unknown words
        val allTokens = ArrayList<Result>(documents.size)
        var maxLength = 0
        for (d in documents) {
            val tokens = tokenizerFactory.create(d.text).tokens
            val tokensFiltered = tokens.filter { wordVectors.hasWord(it) }
            allTokens.add(Result(tokensFiltered, d.label))
            maxLength = Math.max(maxLength, tokensFiltered.size)
        }

        //If longest review exceeds 'truncateLength': only take the first 'truncateLength' words
        if (maxLength > truncateLength) maxLength = truncateLength

        //Create data for training
        //Here: we have documents.size() examples of varying lengths
        val features = Nd4j.create(intArrayOf(allTokens.size, vectorSize, maxLength), 'f')
        val labels = Nd4j.create(intArrayOf(allTokens.size, 2, maxLength), 'f')    //Two labels: positive or negative
        //Because we are dealing with documents of different lengths and only one output at the final time step: use padding arrays
        //Mask arrays contain 1 if data is present at that time step for that example, or 0 if data is just padding
        val featuresMask = Nd4j.zeros(allTokens.size, maxLength)
        val labelsMask = Nd4j.zeros(allTokens.size, maxLength)

        for (i in allTokens.indices) {
            val temp = IntArray(2)
            val it = allTokens[i]
            temp[0] = i
            //Get word vectors for each word in review, and put them in the training data
            var j = 0
            while (j < it.tokens.size && j < maxLength) {
                val token = it.tokens[j]
                val vector = wordVectors.getWordVectorMatrix(token)
                features.put(arrayOf(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j)), vector)

                temp[1] = j
                featuresMask.putScalar(temp, 1.0)  //Word is present (not padding) for this example + time step -> 1.0 in features mask
                j++
            }

            val idx = if (it.label == SentimentLabel.POSITIVE) 0 else 1
            val lastIdx = Math.min(it.tokens.size, maxLength)
            labels.putScalar(intArrayOf(i, idx, lastIdx - 1), 1.0)   //Set label: [0,1] for negative, [1,0] for positive
            labelsMask.putScalar(intArrayOf(i, lastIdx - 1), 1.0)   //Specify that an output exists at the final time step for this example
        }

        return DataSet(features, labels, featuresMask, labelsMask)

    }

    private fun loadDocuments(first: Int, last: Int): List<SentimentDocument> {
        val documents = ArrayList<SentimentDocument>(last - first)
        for (c in first .. last) {
            val reviewNumber = c / 2
            if (c % 2 == 0) {
                val sentence = textIteratorPositive[reviewNumber]
                documents.add(sentence)
            } else {
                val sentence = textIteratorNegative[reviewNumber]
                documents.add(sentence)
            }
        }
        return documents
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
        // we use same count of positive and negative
        return Math.min(textIteratorPositive.size(), textIteratorNegative.size()) * 2
    }

    override fun reset() {
        cursor.set(0)
    }

    override fun hasNext(): Boolean {
        return cursor.get() < totalExamples()
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

    data class Result(val tokens: List<String>, val label: SentimentLabel)
}
