package com.codeabovelab.tpc.tool.nlp

import com.codeabovelab.tpc.core.nn.nlp.*
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Do multiple tasks from NLP for prepare text for learn
 */
class Nlp(private val inDir: String,
          private val outDir: String?
) {
    // slf4j has inefficient backend, which reduce performance
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val inDirPath = Paths.get(inDir)
    private val outDirPath = Paths.get(outDir ?: inDir)
    private val ur = SentenceIteratorImpl.uimaResource(true, true)

    fun run() {
        log.info("Start in {}", inDir)
        Files.walk(inDirPath)
                .filter { it.toString().endsWith(".txt") }
                .forEach(this::processPath)
    }

    private fun processPath(srcPath: Path) {
        log.info("Begin process {}", srcPath)
        var dir = srcPath.parent
        if(inDirPath != outDirPath)
            dir = outDirPath.resolve(dir.relativize(inDirPath))
        val srcFile = srcPath.toFile()
        val destFile = File(dir.toString(), srcFile.nameWithoutExtension + "." + NlpParser.EXT)
        InputStreamReader(FileInputStream(srcFile), StandardCharsets.UTF_8).use { src ->
            OutputStreamWriter(FileOutputStream(destFile), StandardCharsets.UTF_8).use { dst ->
                processStream(src, dst)
                dst.flush()
            }
        }
    }

    private fun  processStream(reader: Reader, writer: Writer) {
        val si = SentenceIteratorImpl.create(ur, ReaderTextIterator("", reader))
        while(si.hasNext()) {
            val sd = si.next()
            if(sd.isNullOrEmpty()) {
                continue
            }
            for(w in sd!!.words) {
                val str = w.str
                if(str.isBlank()) {
                    // it produce token for space and some other unprinted symbols
                    continue
                }
                writer.append(str)
                val hasLemma = w.lemma != null && str.equals(w.lemma, true)
                val hasPos = w.pos != Pos.UNKNOWN
                if(hasPos) {
                    writer.append("|p=")
                    writer.append(w.pos.name)
                }
                if(hasLemma) {
                    writer.append("|l=")
                    writer.append(w.lemma)
                }
                writer.append(" ")
            }
            writer.append("\n")
        }
    }
}