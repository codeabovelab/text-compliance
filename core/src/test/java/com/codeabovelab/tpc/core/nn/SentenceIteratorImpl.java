package com.codeabovelab.tpc.core.nn;

import org.apache.uima.collection.CollectionReader;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.deeplearning4j.text.uima.UimaResource;

import java.util.Collections;
import java.util.List;

/**
 */
class SentenceIteratorImpl extends UimaSentenceIterator implements LabelAwareSentenceIterator {
    public SentenceIteratorImpl(CollectionReader cr, UimaResource ur) {
        super(null, cr, ur);
    }

    @Override
    public void reset() {
        getReader().reset();
    }

    private CollectionReaderImpl getReader() {
        return (CollectionReaderImpl) this.reader;
    }

    @Override
    public String currentLabel() {
        return getReader().getLabel();
    }

    @Override
    public List<String> currentLabels() {
        return Collections.singletonList(currentLabel());
    }

    @Override
    public synchronized String nextSentence() {
        String sentence = super.nextSentence();
        System.out.println("|" + sentence + "|");
        return sentence;
    }
}
