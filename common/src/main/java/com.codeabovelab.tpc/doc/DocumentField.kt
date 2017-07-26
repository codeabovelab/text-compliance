package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Textual

/**
 */
interface DocumentField : Textual {

    interface Builder {
        var id: String?

        var parent: Textual?

        fun build(): DocumentField
    }
}
