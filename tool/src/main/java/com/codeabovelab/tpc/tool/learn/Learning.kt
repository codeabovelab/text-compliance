package com.codeabovelab.tpc.tool.learn

import com.codeabovelab.tpc.core.nn.TokenizerFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.*
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 */
class Learning(
        private val srcDir: String,
        private val destDir: String,
        private val config: String?
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        log.info("Start learning on $srcDir, save data to $destDir.")
        val ld = LearnConfig.learnedDir(destDir)
        val pvf = ld.doc2vec.toFile()
        if (pvf.exists()) {
            log.warn("Destination $destDir is exists, do nothing.")
            return
        }
        pvf.parentFile.mkdirs()
        val lc = LearnConfig()
        val configPath = Paths.get(config)
        lc.configure(configPath) // this method wil create config if absent

        Files.copy(configPath, ld.config, StandardCopyOption.REPLACE_EXISTING)

        val pv = learn(lc)

        log.warn("Save learned data to $destDir.")
        WordVectorSerializer.writeParagraphVectors(pv, pvf)
    }

    private fun learn(lc: LearnConfig): ParagraphVectors {
        val ws = lc.wordSupplier()
        val iter = DirSeqIterator(
                dir = srcDir,
                wordSupplier = ws,
                fileSupport = fileSupport(lc)
        )
        val cache = AbstractCache<VocabWord>()
        val t = TokenizerFactoryImpl()
        val pv = ParagraphVectors.Builder(lc.doc2vec)
                //.iterate(iter)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .build()
        pv.setSequenceIterator(iter)
        pv.fit()
        return pv
    }

    private fun fileSupport(lc: LearnConfig): Map<String, (Path) -> SentenceIterator?> {
        val ur = lc.createUimaResource()
        return mapOf(
            Pair("txt", { path ->
                try {
                    SentenceIteratorImpl.create(ur, FileTextIterator(path))
                } catch (e: IOException) {
                    throw RuntimeException("On read " + path,  e)
                }
            }),
            Pair(NlpParser.EXT, { path ->
                NlpTextSentenceIter.create(path)
            })
        )
    }

}

