package com.codeabovelab.tpc.tool.learn.sentiment

import com.codeabovelab.tpc.tool.learn.LearnConfig
import com.codeabovelab.tpc.tool.learn.PerfomanceSettings
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors
import org.deeplearning4j.nn.conf.GradientNormalization
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.WorkspaceMode
import org.deeplearning4j.nn.conf.layers.GravesLSTM
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.learning.config.Adam
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths
import java.time.Duration

/**
 */
class SentimentLearning(
        private val srcDir: String,
        private val srcVectorDir: String,
        private val destDir: String,
        private val config: String?
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        log.info("Start learning on $srcDir, srcVectorDir $srcVectorDir, save data to $destDir.")
        val ld = LearnConfig.learnedDir(destDir)
        val pvf = ld.doc2vec.toFile()
        if (pvf.exists()) {
            log.warn("Destination $destDir exists, do nothing.")
            return
        }
        pvf.parentFile.mkdirs()
        val lc = LearnConfig()
        if (config != null) {
            val srcConfigPath = Paths.get(config).toAbsolutePath()
            lc.configure(srcConfigPath) // this method will create config if absent
            lc.save(ld.config)
        }

        learn(lc)
        log.warn("Save learned data to $destDir.")
    }

    private fun learn(lc: LearnConfig) {
        PerfomanceSettings.useWorkspacesGC()
        PerfomanceSettings.useCuda()
        val conf = NeuralNetConfiguration.Builder()
                .updater(Adam.builder().beta1(0.9).beta2(0.999).build())
                .regularization(true).l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0)
                .learningRate(lc.sentiment.learningRate)
                .trainingWorkspaceMode(WorkspaceMode.SEPARATE).inferenceWorkspaceMode(WorkspaceMode.SEPARATE) //https://deeplearning4j.org/workspaces
                .list()
                .layer(0, GravesLSTM.Builder().nIn(lc.sentiment.vectorSize)
                        .nOut(lc.sentiment.truncateReviewsToLength)
                        .activation(Activation.TANH).build())
                .layer(1, RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(lc.sentiment.truncateReviewsToLength)
                        .nOut(2).build())
                .pretrain(false).backprop(true).build()
        val net = MultiLayerNetwork(conf)
        net.init()
        net.setListeners(ScoreIterationListener(lc.sentiment.nEpochs))
        //DataSetIterators for training and testing respectively
        val wordVectors = WordVectorSerializer.loadStaticModel(File(srcVectorDir))

        val train = getIterator("train", lc, wordVectors)
        val test = getIterator("test", lc, wordVectors)

        log.info("Starting training")
        for (i in 0 until lc.sentiment.nEpochs) {
            var start = System.currentTimeMillis()
            log.info("Epoch {} started", i)
            net.fit(train)
            train.reset()
            log.info("Epoch {} completed for {} minutes. Starting evaluation:", i, Duration.ofMillis(start - System.currentTimeMillis()))
            start = System.currentTimeMillis()
            val evaluation = net.evaluate(test)
            test.reset()
            log.info("evaluation {} completed for {}.", i, Duration.ofMillis(start - System.currentTimeMillis()))
            log.info("stats: {}", evaluation.stats())
            ModelSerializer.writeModel(net, File(destDir, "res--$i.zip"), false)
        }

    }

    private fun getIterator(dir: String, lc: LearnConfig, wordVectors: WordVectors): DataSetIterator {

        //AsyncDataSetIterator(
                return SentimentIterator(
                        wordVectors = wordVectors,
                        batchSize = lc.sentiment.batchSize,
                        dataPath = Paths.get(srcDir, dir),
                        truncateLength = lc.sentiment.truncateReviewsToLength)
               // )
    }

}

