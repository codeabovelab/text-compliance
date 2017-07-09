package com.codeabovelab.tpc.tool.learn.sentiment

import com.codeabovelab.tpc.tool.learn.LearnConfig
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.nn.conf.GradientNormalization
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater
import org.deeplearning4j.nn.conf.layers.GravesLSTM
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

/**
 */
class SentimentLearning(
        private val srcTrainDir: String,
        private val srcTestDir: String,
        private val srcVectorDir: String,
        private val destDir: String,
        private val config: String?
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        log.info("Start learning on $srcVectorDir, save data to $destDir.")
        val ld = LearnConfig.learnedDir(destDir)
        val pvf = ld.doc2vec.toFile()
        if (pvf.exists()) {
            log.warn("Destination $destDir exists, do nothing.")
            return
        }
        pvf.parentFile.mkdirs()
        val lc = LearnConfig()
        if(config != null) {
            val srcConfigPath = Paths.get(config).toAbsolutePath()
            lc.configure(srcConfigPath) // this method will create config if absent
            lc.save(ld.config)
        }

        learn(lc)
        log.warn("Save learned data to $destDir.")
    }

    private fun learn(lc: LearnConfig) {

        val conf = NeuralNetConfiguration.Builder()
                .updater(Updater.ADAM).adamMeanDecay(0.9).adamVarDecay(0.999)
                .regularization(true).l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0)
                .learningRate(lc.sentiment.learningRate)
                .list()
                .layer(0, GravesLSTM.Builder().nIn(lc.sentiment.vectorSize).nOut(256)
                        .activation(Activation.TANH).build())
                .layer(1, RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(256).nOut(2).build())
                .pretrain(false).backprop(true).build()
        val net = MultiLayerNetwork(conf)
        net.init()
        net.setListeners(ScoreIterationListener(1))
        //DataSetIterators for training and testing respectively
        val wordVectors = WordVectorSerializer.loadStaticModel(File(srcVectorDir))

        val train = SentimentIterator(
                wordVectors = wordVectors,
                batchSize = lc.sentiment.batchSize,
                textIterator = SentimentDocumentFilesArray(srcTrainDir),
                truncateLength = lc.sentiment.truncateReviewsToLength)

        val test = SentimentIterator(
                wordVectors = wordVectors,
                batchSize = lc.sentiment.batchSize,
                textIterator = SentimentDocumentFilesArray(srcTestDir),
                truncateLength = lc.sentiment.truncateReviewsToLength)

        log.info("Starting training")
        for (i in 0..lc.sentiment.nEpochs - 1) {
            net.fit(train)
            train.reset()
            log.info("Epoch {} completed. Starting evaluation:", i)

            val evaluation = Evaluation()
            while (test.hasNext()) {
                val t = test.next()
                val features = t.features
                val labels = t.labels
                val inMask = t.featuresMaskArray
                val outMask = t.labelsMaskArray
                val predicted = net.output(features, false, inMask, outMask)

                evaluation.evalTimeSeries(labels, predicted, outMask)
            }
            test.reset()

            log.info("stats: {}", evaluation.stats())
        }

        ModelSerializer.writeModel(net, File(destDir), false)

    }

}

