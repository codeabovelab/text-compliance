package com.codeabovelab.tpc.core.nn.nlp

import com.codeabovelab.tpc.text.Text
import org.deeplearning4j.text.uima.UimaResource

/**
 */
class SentenceIteratorFactoryImpl(
        private val uima : UimaResource
) : SentenceIteratorFactory {

    override fun create(text: Text): SentenceIterator {
        return SentenceIteratorImpl.create(uima, TextIterator.singleton(text))
    }
}