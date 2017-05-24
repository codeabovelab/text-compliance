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
import org.deeplearning4j.text.documentiterator.DocumentIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareDocumentIterator;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 */
@Slf4j
public class NNTest {


    @BeforeClass
    public static void beforeClass() {

    }

    @Test
    @Ignore
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
        System.out.println(pv.similarity("DOC_<436524.1075857649853.JavaMail.evans@thyme>", "DOC_<22480806.1075857604258.JavaMail.evans@thyme>"));
        System.out.println(pv.similarity("DOC_<13395955.1075852729395.JavaMail.evans@thyme>", "DOC_<22480806.1075857604258.JavaMail.evans@thyme>"));
    }

    private ParagraphVectors loadOrCreate(String workDir, File pvf) throws Exception {
        ParagraphVectors pv;
        if(pvf.exists()) {
            log.warn("DB is exists loading.");
            pv = WordVectorSerializer.readParagraphVectors(pvf);
        } else {
            log.warn("DB is non exists creating.");
            DocumentIterator iter = new MyLabelAwareDocumentIterator(workDir + "/emails");

            AbstractCache<VocabWord> cache = new AbstractCache<>();

            TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new CommonPreprocessor());

            pv = new ParagraphVectors.Builder()
              .minWordFrequency(5)
              .iterations(5)
              .epochs(1)
              .layerSize(100)
              .learningRate(0.025)
              .windowSize(5)
              .iterate(iter)
              .trainWordVectors(true)
              //.trainWordVectors(false)
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

        private static final Pattern SKIP_HTML = Pattern.compile("<[\\S][^>]*>", Pattern.DOTALL);
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
            return "DOC_" + doc.getId();
        }

        @Override
        public InputStream nextDocument() {
            counter++;
            Path path = fileIter.next();
            log.info("Choose {}-th mail at path {}", counter, path);
            processEmail(path);
            String str = ((DocumentImpl)doc).getBody().getData();
            // workaround to skip html tags
            str = SKIP_HTML.matcher(str).replaceAll("");
            int b = str.indexOf("@");
            if(str.startsWith("null")) {
                int width = 20;
                System.out.println(str.substring(Math.max(b - width, 0), Math.min(b + width, str.length())));
            }
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
