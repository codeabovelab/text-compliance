package com.codeabovelab.tpc.core.nn;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.annotator.SentenceAnnotator;
import org.deeplearning4j.text.annotator.TokenizerAnnotator;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.text.uima.UimaResource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 */
@Slf4j
@Ignore
public class NNTest {


    @BeforeClass
    public static void beforeClass() {

    }

    @Test
    public void test() throws Exception {
        String workDir = "/home/rad/tmp/nn-data/";

        ParagraphVectors pv = loadOrCreate(workDir, new File(workDir + "ParagraphVectors.zip"));

        String[] patterns = new String[]{"company", "employee", "money", "we"};
        for(String pattern: patterns) {
            Collection<String> words = pv.wordsNearest(pattern, 10);
            System.out.println(pattern + " has similar words: " + words);
        }
        //tasks:
        // implement sentence delimiter (for NN learning & analysis)
        // implement "text cleaner" - especially for emails, we must consider "forward" & citation
        // find way to differentiate DOC from words in this network or use different network for words
        String src = "I would like to apply for the position advertised in The Guardian of 12 May for a Personal Assistant to the Sales Director.\n" +
          "As you will see from my curriculum vitae, much of the work I do in my present position is that of a PA. I deal not only with the routine work of a secretary, but also represent the Assistant Director at small meetings and am delegated to take a number of policy decisions in his absence.\n" +
          "Your advertisement asked for some knowledge of languages. I have kept up my French, and learnt German for the past three years at evening classes, and have regularly visited Belgium and Germany with the Assistant Director, acting as an interpreter and translator for him.\n" +
          "I am particularly interested in the situation you are offering, as I would like to become more involved in an information technology organization. I am quite familiar with many of the software products that ICS manufactures for office technology.\n" +
          "As well as my secretarial skills and experience of running a busy office, I am used to working with technicians and other specialized personnel in the field of computers. I have a genuine interest in computer development and the people involved in the profession.\n" +
          "Please let me know if there is any further information you require. I look forward to hearing from you.";
        String[] texts = new String[]{
          src,
          "What you doing ant this night? " + src,
          src + " What you doing ant this night? ",
          src + " What the hell? ",

        };
        int i = 0;
        for(String text: texts) {
            System.out.println(i++);
            // wait a https://github.com/deeplearning4j/deeplearning4j/issues/3447
            // or make our ParagraphVectors implementation
            List<String> labels = (List<String>) pv.nearestLabels(text, 10);
            System.out.println(labels);
            printSimilarity(pv, text, labels.get(0));
            printSimilarity(pv, text, labels.get(1));
            printSimilarity(pv, text, labels.get(3));

        }
    }

    private void printSimilarity(ParagraphVectors pv, String sentence, String label) {
        System.out.println(label + " similarity :" + pv.similarityToLabel(sentence, label));
    }


    private ParagraphVectors loadOrCreate(String workDir, File pvf) throws Exception {
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        ParagraphVectors pv;
        if(pvf.exists()) {
            log.warn("DB is exists loading.");
            pv = WordVectorSerializer.readParagraphVectors(pvf);
            pv.setTokenizerFactory(t);
        } else {
            log.warn("DB is non exists creating.");
            LabelAwareSentenceIterator iter = new SampleSentenceIterator(workDir + "/manually");
            AbstractCache<VocabWord> cache = new AbstractCache<>();
            pv = new ParagraphVectors.Builder()
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
              .sampling(0)
              .build();

            pv.fit();

            WordVectorSerializer.writeParagraphVectors(pv, pvf);
        }
        return pv;
    }

    private UimaSentenceIterator createIter(String dir) throws Exception {
        UimaResource ur = new UimaResource(AnalysisEngineFactory.createEngine(AnalysisEngineFactory
          .createEngineDescription(TokenizerAnnotator.getDescription(),
            SentenceAnnotator.getDescription())));
        CollectionReader cr = new CollectionReaderImpl(dir);
        return new SentenceIteratorImpl(cr, ur);
    }


}
