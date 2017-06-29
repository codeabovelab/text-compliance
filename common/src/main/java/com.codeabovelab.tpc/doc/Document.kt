package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.Textual

/**
 */
interface Document : Textual {
    interface Builder {
        var body: Text?
        fun build(): Document
    }
}
