package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.text.TextImpl
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache
import org.junit.Test
import org.junit.Ignore
import org.slf4j.LoggerFactory

import java.io.*

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

        val tc = TextClassifier(vectorsFile = filePath, maxLabels = 10)

        val src = "I would like to apply for the position advertised in The Guardian of 12 May for a Personal Assistant to the Sales Director.\n" +
          "As you will see from my curriculum vitae, much of the work I do in my present position is that of a PA. I deal not only with the routine work of a secretary, but also represent the Assistant Director at small meetings and am delegated to take a number of policy decisions in his absence.\n" +
          "Your advertisement asked for some knowledge of languages. I have kept up my French, and learnt German for the past three years at evening classes, and have regularly visited Belgium and Germany with the Assistant Director, acting as an interpreter and translator for him.\n" +
          "I am particularly interested in the situation you are offering, as I would like to become more involved in an information technology organization. I am quite familiar with many of the software products that ICS manufactures for office technology.\n" +
          "As well as my secretarial skills and experience of running a busy office, I am used to working with technicians and other specialized personnel in the field of computers. I have a genuine interest in computer development and the people involved in the profession.\n" +
          "Please let me know if there is any further information you require. I look forward to hearing from you.";
        val texts = arrayOf(
              src,
              "What you doing ant this night? " + src,
              src + " What you doing ant this night? ",
              src + " What the hell? "
            )
        var i = 0
        val pc = PredicateContext(document = DocumentImpl.builder().build(), attributes = emptyMap())
        for(text in texts) {
            System.out.println(i++)
            val res = tc.test(pc, TextImpl("sample_" + i, text))
            System.out.println(res.labels)
        }
    }

    fun createIfNeed(workDir: String, filePath: String) {
        var pv: ParagraphVectors? = null
        val pvf = File(filePath)
        if(pvf.exists()) {
            log.warn("DB is exists, do nothing.");
            return;
        }
        log.warn("DB is non exists, creating.");
        val iter = SampleSentenceIterator(workDir + "/manually")
        val cache = AbstractCache<VocabWord>()
        val t = createTokenizerFactory()
        pv = ParagraphVectors.Builder()
          .minWordFrequency(3)
          .iterations(5)
          .epochs(1)
          .layerSize(100)
          .learningRate(0.025)
          .windowSize(10)
          .iterate(iter)
          .trainWordVectors(true)
          .trainSequencesRepresentation(true)
          .vocabCache(cache)
          .tokenizerFactory(t)
          .sampling(0.0)
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
