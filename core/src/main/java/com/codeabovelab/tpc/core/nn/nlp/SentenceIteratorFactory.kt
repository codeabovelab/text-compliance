package com.codeabovelab.tpc.core.nn.nlp

import com.codeabovelab.tpc.text.Text

/**
 */
interface SentenceIteratorFactory {

    companion object {
        val STUB = object : SentenceIteratorFactory {

            override fun create(text: Text): SentenceIterator {
                throw NotImplementedError("It is a stub.")
            }
        }
    }

    fun create(text: Text): SentenceIterator
}