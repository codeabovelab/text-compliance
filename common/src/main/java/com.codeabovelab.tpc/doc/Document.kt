package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.Textual

/**
 */
interface Document : Textual {
    interface Builder<B: Builder<B>> : Textual.Builder<B> {
        var body: Text?
        /**
         * Map for document fields & other attributes
         */
        val attributes: MutableMap<String, Any?>
        override fun build(): Document
    }

    val attributes: Map<String, Any?>
}
