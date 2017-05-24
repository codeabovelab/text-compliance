package com.codeabovelab.tpc.core.nn;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.IOException;

/**
 */
class CollectionReaderImpl extends JCasCollectionReader_ImplBase {

    private DocIter iter;

    public CollectionReaderImpl(String dir) throws Exception {
        iter = new DocIter(dir);
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return iter.hasNext();
    }

    @Override
    public Progress[] getProgress() {
        Progress progress = new ProgressImpl(iter.getCounter(), Integer.MAX_VALUE, Progress.ENTITIES);
        return new Progress[]{progress};
    }

    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException {
        String str = iter.nextDocument();

        // set the document's text
        jCas.setDocumentText(str);
    }

    public void reset() {
        iter.reset();
    }

    public String getLabel() {
        return iter.currentLabel();
    }
}
