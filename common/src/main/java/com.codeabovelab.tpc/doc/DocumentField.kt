package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Textual

/**
 */
interface DocumentField : Textual {

    interface Builder {
        fun build(document: Document.Builder): DocumentFieldImpl
    }

    /**
     * Filed name. Note that it not does not include document id.
     * @return non null string
     */
    val name: String
}
