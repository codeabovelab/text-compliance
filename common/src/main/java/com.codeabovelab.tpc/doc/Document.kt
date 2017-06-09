package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Textual

/**
 */
interface Document : Textual {
    interface Builder {
        var id: String?
        fun build(): Document
    }
}
