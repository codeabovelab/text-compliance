package com.codeabovelab.tpc.core.nn;

import com.codeabovelab.tpc.doc.Document;
import com.codeabovelab.tpc.doc.DocumentImpl;
import com.codeabovelab.tpc.integr.email.EmailToDocument;
import com.codeabovelab.tpc.text.TextualUtil;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelAwareDocumentIterator;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 */
@Slf4j
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
    }

    private ParagraphVectors loadOrCreate(String workDir, File pvf) throws Exception {
        ParagraphVectors pv;
        if(pvf.exists()) {
            log.warn("DB is exists loading.");
            pv = WordVectorSerializer.readParagraphVectors(pvf);
        } else {
            log.warn("DB is non exists creating.");
            LabelAwareDocumentIterator iter = new MyLabelAwareDocumentIterator(workDir);

            AbstractCache<VocabWord> cache = new AbstractCache<>();

            TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new CommonPreprocessor());

            pv = new ParagraphVectors.Builder()
              .minWordFrequency(1)
              .iterations(5)
              .epochs(1)
              .layerSize(100)
              .learningRate(0.025)
              .windowSize(5)
              .iterate(iter)
              .trainWordVectors(false)
              .vocabCache(cache)
              .tokenizerFactory(t)
              .sampling(0)
              .build();

            pv.fit();

            WordVectorSerializer.writeParagraphVectors(pv, pvf);
        }
        return pv;
    }


    private static class MyLabelAwareDocumentIterator implements LabelAwareDocumentIterator {

        private final EmailToDocument etd = new EmailToDocument();
        private final String dir;
        private Document doc;
        private Iterator<Path> fileIter;
        private int counter;

        public MyLabelAwareDocumentIterator(String dir) throws Exception {
            this.dir = dir;
            reset();
        }

        private void processEmail(Path path) {
            try(InputStream is = new FileInputStream(path.toFile())) {
                this.doc = etd.apply(is);
            } catch (Exception e) {
                log.error("Can not read: {} due to error: {}", path, e.toString());
            }
        }

        @Override
        public String currentLabel() {
            return doc.getId();
        }

        @Override
        public InputStream nextDocument() {
            Path path = fileIter.next();
            log.info("Choose {}-th mail at path {}", counter, path);
            counter++;
            processEmail(path);
            String str = ((DocumentImpl)doc).getBody().getData();
            return new ByteArrayInputStream(str.getBytes());
        }

        @Override
        public boolean hasNext() {
            return fileIter.hasNext();
        }

        @Override
        public void reset() {
            try {
                counter = 0;
                Stream<Path> stream = Files.walk(Paths.get(dir))
                  .filter(path -> path.toString().endsWith(".eml"))
                  // without limit we get 'java.lang.OutOfMemoryError: GC overhead limit exceeded'
                  .limit(10000L);
                this.fileIter = stream.iterator();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }
}
