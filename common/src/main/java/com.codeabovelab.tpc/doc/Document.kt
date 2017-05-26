package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Textual

/**
 */
interface Document : Textual {
    interface Builder {
        val id: String?
        fun build(): Document
    }
}
