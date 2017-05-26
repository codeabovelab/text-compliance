package com.codeabovelab.tpc.core.nn;

import com.codeabovelab.tpc.text.Text;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.cleartk.opennlp.tools.SentenceAnnotator;
import org.deeplearning4j.text.annotator.TokenizerAnnotator;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.deeplearning4j.text.uima.UimaResource;

import java.io.IOException;
import java.util.List;

/**
 * TODO extract word sequence offsets
 */
class SentenceIteratorImpl extends UimaSentenceIterator implements LabelAwareSentenceIterator {

    private SentenceIteratorImpl(CollectionReader cr, UimaResource ur) {
        super(null, cr, ur);
    }

    public static SentenceIteratorImpl create(TextIterator iter) throws Exception {
        UimaResource ur = new UimaResource(AnalysisEngineFactory.createEngine(AnalysisEngineFactory
          .createEngineDescription(TokenizerAnnotator.getDescription(),
            SentenceAnnotator.getDescription())));
        CollectionReader cr = new CollectionReaderImpl(iter);
        return new SentenceIteratorImpl(cr, ur);
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
        return null;
    }

    @Override
    public List<String> currentLabels() {
        return getReader().getLabels();
    }
}

class CollectionReaderImpl extends JCasCollectionReader_ImplBase {

    private TextIterator iter;

    public CollectionReaderImpl(TextIterator iter) throws Exception {
        this.iter = iter;
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return iter.hasNext();
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[]{};
    }

    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException {
        Text text = iter.next();

        // set the document's text
        jCas.setDocumentText(text.getData().toString());
    }

    void reset() {
        iter.reset();
    }

    List<String> getLabels() {
        return iter.getLabels();
    }
}
