package com.codeabovelab.tpc.core.nn.nlp

/**
 * Hierarchy of this class is need for hide 'setters' from users of WordContext.
 */
class WordContext private constructor() {

    private val handler = Handler()

    val sentence: SentenceData
        get() = handler.sentence!!

    val word: WordData
        get() = handler.word!!


    inner class Handler {
        var sentence: SentenceData? = null
        var word: WordData? = null
        val context: WordContext
            get() = this@WordContext
    }

    companion object {
        fun create(): Handler {
            val wc = WordContext()
            return wc.handler
        }
    }
}