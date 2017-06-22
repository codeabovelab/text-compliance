package com.codeabovelab.tpc.tool.learn

import com.codeabovelab.tpc.core.nn.TokenizerFactoryImpl
import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.tool.util.Config
import com.codeabovelab.tpc.tool.util.Copy
import com.codeabovelab.tpc.util.Reflections
import org.apache.commons.io.FileUtils
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
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
            log.warn("Destination $destDir is exists, do nothing.")
            return
        }
        pvf.parentFile.mkdirs()
        val lc = LearnConfig()
        if(config != null) {
            val srcConfigPath = Paths.get(config).toAbsolutePath()
            lc.configure(srcConfigPath) // this method wil create config if absent
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

    private fun fileSupport(lc: LearnConfig): DirSeqIterator.FileSupport {
        val ur = lc.createUimaResource()
        val map = mapOf<String, (Path) -> SentenceIterator?>(
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
        val fi = { stream: Stream<Path> ->
            val files = stream.reduce(HashMap<String, Path>(), { map, path ->
                val pathStr = path.toString()
                val ext = pathStr.substringAfterLast('.')
                val str = pathStr.substringBeforeLast('.')
                if (ext == NlpParser.EXT) {
                    // we must prefer NlpParser.EXT to other
                    map.put(str, path)
                } else {
                    map.putIfAbsent(str, path)
                }
                map
            }, { left, _ ->
                //we suppose that 'left' same as '_'
                left
            }).values
            files.stream().sorted().iterator()
        }
        return DirSeqIterator.FileSupport(
                map = map,
                filesIterator = fi
        )
    }

    private fun copyResources(fromPath: Path, toPath: Path, lc: LearnConfig) {
        Reflections.forEach(lc) {
            val copyAnn = this.property.findAnnotation<Copy>()
            if(copyAnn != null) {
                val strPath = this.propertyValue as String
                val from = fromPath.resolve(strPath)
                val to = toPath.resolve(copyAnn.dir)
                //Files.createDirectories(to.parent)
                FileUtils.copyDirectory(from.toFile(), to.toFile())
                this.propertyValue = copyAnn.dir
                false
            } else {
                true
            }
        }
    }
}

