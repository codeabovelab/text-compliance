package com.codeabovelab.tpc.core.nn;

import com.codeabovelab.tpc.util.Sugar;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Simple iterator for NN teaching on directory of sample data.
 */
@Slf4j
public class SampleSentenceIterator extends BaseSentenceIterator implements LabelAwareSentenceIterator {
    private static final String DELIMITER = "--";
    private static final String LABELS = "labels:";
    private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();
    private final String dir;
    private int counter;
    private Iterator<Path> fileIter;
    private Iterator<String> sentenceIter;
    private List<String> labels;

    public SampleSentenceIterator(String dir) {
        this.dir = dir;
        reset();
    }

    @Override
    public String currentLabel() {
        return Sugar.isEmpty(labels)? null : labels.get(0);
    }

    @Override
    public List<String> currentLabels() {
        return labels == null? Collections.emptyList() : labels;
    }

    @Override
    public String nextSentence() {
        String sentence = null;
        if(sentenceIter != null && sentenceIter.hasNext()) {
            sentence = sentenceIter.next();
        } else {
            sentenceIter = nextSentenceIter();
            if(sentenceIter != null) {
                sentence = sentenceIter.next();
            }
        }
        //System.out.println(currentLabels() + ": " + sentence);
        return sentence;
    }

    private Iterator<String> nextSentenceIter() {
        Path file = fileIter.next();
        final Iterator<String> strings;
        try {
            strings = Files.lines(file).iterator();
        } catch (IOException e) {
            throw new RuntimeException("On read " + file,  e);
        }
        return new InFileIterator(strings);
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
              .filter(path -> path.toString().endsWith(".txt"));
            this.fileIter = stream.iterator();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private class InFileIterator implements Iterator<String> {

        private final Iterator<String> strings;

        InFileIterator(Iterator<String> strings) {
            this.strings = strings;
        }

        @Override
        public boolean hasNext() {
            return strings.hasNext();
        }

        @Override
        public String next() {
            if(!strings.hasNext()) {
                return "";
            }
            String line = strings.next().trim();
            if(line.startsWith(DELIMITER)) {
                parseLabels();
                line = null;
                if(strings.hasNext()) {
                    line = strings.next().trim();
                }
            }
            return line;
        }

        private void parseLabels() {
            String line;
            while(strings.hasNext()) {
                line = strings.next();
                if(line.startsWith(LABELS)) {
                    labels = SPLITTER.splitToList(line.substring(LABELS.length()));
                    break;
                }
            }
        }
    }
}
