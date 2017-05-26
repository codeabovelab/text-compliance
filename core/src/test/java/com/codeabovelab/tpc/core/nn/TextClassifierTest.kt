package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.text.TextImpl
import com.google.common.io.Resources
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.junit.Test
import org.junit.Ignore
import org.slf4j.LoggerFactory

import java.io.*
import java.nio.charset.StandardCharsets

/**
 */
@Ignore
class TextClassifierTest {

    val log = LoggerFactory.getLogger(this.javaClass)!!

    @Test
    fun test() {
        val workDir = "/home/rad/tmp/nn-data/";
        val filePath = workDir + "ParagraphVectors.zip";

        createIfNeed(workDir, filePath)

        val tc = TextClassifier(vectorsFile = filePath, maxLabels = 3)

        val texts = Resources.readLines(Resources.getResource(this.javaClass, "samples.txt"), StandardCharsets.UTF_8)
        var i = 0
        val pc = PredicateContext(document = DocumentImpl.builder().build(), attributes = emptyMap())
        for(text in texts) {
            System.out.println(i++)
            val res = tc.test(pc, TextImpl("sample_" + i, text))
            res.entries.forEach {
                System.out.println(it.coordinates.toString() + ": " + it.labels)
            }
        }
    }

    fun createIfNeed(workDir: String, filePath: String) {
        val pvf = File(filePath)
        if(pvf.exists()) {
            log.warn("DB is exists, do nothing.")
            return
        }
        log.warn("DB is non exists, creating.")
        val iter = SampleSentenceIterator(workDir + "/manually")
        val cache = AbstractCache<VocabWord>()
        val t = createTokenizerFactory()
        var pv = ParagraphVectors.Builder()
          .minWordFrequency(3)
          .iterations(5)
          .epochs(2)
          .layerSize(300)
          .learningRate(0.025)
          .windowSize(7)
          .iterate(iter)
          .trainWordVectors(true)
          .trainSequencesRepresentation(true)
          .vocabCache(cache)
          .tokenizerFactory(t)
          .negativeSample(7.0)
          .sampling(1E-5)
          .build()

        pv.fit()

        WordVectorSerializer.writeParagraphVectors(pv, pvf)
    }


//    fun createIter(String dir): UimaSentenceIterator  {
//        UimaResource ur = new UimaResource(AnalysisEngineFactory.createEngine(AnalysisEngineFactory
//          .createEngineDescription(TokenizerAnnotator.getDescription(),
//            SentenceAnnotator.getDescription())));
//        CollectionReader cr = new CollectionReaderImpl(dir);
//        return new SentenceIteratorImpl(cr, ur);
//    }


}
