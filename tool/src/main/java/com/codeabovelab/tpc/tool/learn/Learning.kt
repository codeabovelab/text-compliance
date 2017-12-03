package com.codeabovelab.tpc.tool.learn

import com.codeabovelab.tpc.core.nn.tokenizerFactory
import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.tool.util.Copy
import com.codeabovelab.tpc.util.Reflections
import org.apache.commons.io.FileUtils
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.full.findAnnotation

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
            log.warn("Destination $destDir exists, do nothing.")
            return
        }
        pvf.parentFile.mkdirs()
        val lc = LearnConfig()
        if(config != null) {
            val srcConfigPath = Paths.get(config).toAbsolutePath()
            lc.configure(srcConfigPath) // this method will create config if absent
            copyResources(srcConfigPath.parent, ld.root, lc)
            lc.save(ld.config)
        }

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
        val t = tokenizerFactory()
        val pv = ParagraphVectors.Builder(lc.doc2vec)
                //.iterate(iter)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .build()
        pv.unk = UnkDetector.UNK
        pv.setSequenceIterator(iter)
        pv.fit()
        return pv
    }

    private fun fileSupport(lc: LearnConfig): DirSeqIterator.FileSupport {
        val ur = UimaFactory.create(lc.createUimaRequest())
        val map = mapOf<String, (Path) -> SentenceIterator?>(
                Pair("txt", { path ->
                    val fti = try {
                        FileTextIterator(path)
                    } catch (e: IOException) {
                        throw RuntimeException("On read " + path,  e)
                    }
                    SentenceIteratorImpl.create(ur, fti)
                }),
                Pair(NlpParser.EXT, { path ->
                    NlpTextSentenceIter.create(path)
                })
        )
        fun filesIterator(stream: Stream<Path>): Iterator<Path> {
            // we use tree map for provide order
            val paths = TreeMap<String, Path>()
            stream.forEach({ path ->
                val pathStr = path.toString()
                val ext = pathStr.substringAfterLast('.')
                val str = pathStr.substringBeforeLast('.')
                if (ext == NlpParser.EXT) {
                    // we must prefer NlpParser.EXT to other
                    paths.put(str, path)
                } else {
                    paths.putIfAbsent(str, path)
                }
            })
            return paths.values.iterator()
        }
        return DirSeqIterator.FileSupport(
                map = map,
                filesIterator = ::filesIterator
        )
    }

    private fun copyResources(fromPath: Path, toPath: Path, lc: LearnConfig) {
        Reflections.forEachRecursive(lc) {
            val copyAnn = this.property.findAnnotation<Copy>()
            if(copyAnn != null) {
                val value = this.propertyValue
                if(value is String) {
                    val strPath = value
                    val from = fromPath.resolve(strPath)
                    val to = toPath.resolve(copyAnn.dir)
                    FileUtils.copyDirectory(from.toFile(), to.toFile())
                }
                this.propertyValue = copyAnn.dir
                false
            } else {
                true
            }
        }
    }
}

